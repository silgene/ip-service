package com.nginx.ip.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nginx.ip.config.AliConfig;
import com.nginx.ip.constant.RegionConstants;
import com.nginx.ip.mapper.IpChatMapper;
import com.nginx.ip.pojo.entity.IpChat;
import com.nginx.ip.service.IpChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * 聊天相关的IP统计，目前还不需要，暂时留着，日后可能拓展
 * TODO
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IpChatServiceImpl implements IpChatService {

    private final AliConfig aliConfig;

    private final RestTemplate restTemplate;

    private final IpChatMapper ipChatMapper;


    /**
     * 保存或更新用户的 IP 数据
     *
     * @param clientIp 客户端 IP
     */
    @Override
    public void saveOrUpdateIpChat(String clientIp, String url) throws Exception {
        log.info("收到ip地址: {}", clientIp);
        LocalDateTime currentDateTime = LocalDateTime.now();

        QueryWrapper<IpChat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip_address", clientIp);
        queryWrapper.between("date", currentDateTime.toLocalDate().atStartOfDay(), currentDateTime.toLocalDate().atTime(23, 59, 59));

        IpChat existingIpChat = ipChatMapper.selectOne(queryWrapper);
        // 更新访问次数和更新时间字段
        if (existingIpChat != null) {
            existingIpChat.setAccessCount(existingIpChat.getAccessCount() + 1);
            existingIpChat.setUpdateTime(LocalDateTime.now());
            ipChatMapper.updateById(existingIpChat);
        } else {
            IpChat newIpChat = createIpChat(clientIp, url);
            ipChatMapper.insert(newIpChat);
        }
    }

    /**
     * 创建新的 IpChat 对象，远程调用阿里云API获取 IP 地址信息
     *
     * @param clientIp  客户端 IP
     * @param originURL 原始 URL
     * @return
     * @throws Exception
     */
    private IpChat createIpChat(String clientIp, String originURL) throws Exception {
        IpChat newIpChat = new IpChat();

        // 阿里云 API 端点
        String url = aliConfig.getUrl();
        String appCode = aliConfig.getAppcode();

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE " + appCode);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("ip", clientIp);

        // 发送 API 请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        JsonNode data = jsonResponse.get("data");

        // 提取数据
        String region = data.get("region").asText();
        String country = data.get("country").asText();
        String city = data.get("city").asText();

        // 从 RegionConstants 获取标准化地区名称，只有中国需要修改省份
        if (country.equals("中国")) {
            region = RegionConstants.REGION_MAPPING.getOrDefault(region, region + "省");
        } else if (country.equals("保留")) { // 局域网内ip，直接设置局域网默认地域信息
            country = "中国";
            region = "广东省";
            city = "深圳";
        }
        // 存储数据
        newIpChat.setIpAddress(clientIp);
        newIpChat.setRegionName(region);
        newIpChat.setCountry(country);
        newIpChat.setCity(city);
        newIpChat.setDate(LocalDateTime.now());
        newIpChat.setUpdateTime(LocalDateTime.now());
        newIpChat.setAccessCount(1);
        newIpChat.setUrl(originURL); // 设置原始URL

        return newIpChat;
    }

    /**
     * 获取指定时间范围内的平均聊天次数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public double getAverageChatCount(LocalDateTime startTime, LocalDateTime endTime) {
        return ipChatMapper.getAverageChatCount(startTime, endTime);
    }

}

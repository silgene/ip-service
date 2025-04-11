package com.nginx.ip.controller;

import com.nginx.ip.pojo.model.RestResponse;
import com.nginx.ip.service.IpChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/IpChat")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "聊天IP统计接口")
public class IpChatController {

    private final IpChatService ipChatService;

    /**
     * 保存或更新 用户聊天的 IP 数据
     *
     * @param clientIp 客户端 IP
     * @return
     */
    @PostMapping("/ip-statistics")
    @Operation(summary = "保存或更新IP数据")
    public RestResponse<String> saveOrUpdateIpData(@RequestParam("ip") String clientIp, @RequestParam("url") String url) throws Exception {
        ipChatService.saveOrUpdateIpChat(clientIp, url);
        return RestResponse.success("IP 数据已保存");
    }

    /**
     * 获取平均聊天次数
     * TODO 还未接入nginx的/sztuerollapi/chat/v1
     */
    @GetMapping("/average-chat")
    @Operation(summary = "获取平均聊天次数")
    public RestResponse<Double> getAverageChatCount(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);
        return RestResponse.success(ipChatService.getAverageChatCount(startTime, endTime));
    }
}

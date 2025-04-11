package com.nginx.ip.controller;

import com.nginx.ip.pojo.model.RestResponse;
import com.nginx.ip.service.IpChatService;
import com.nginx.ip.service.IpDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "访问IP统计接口")
public class IpDataController {
    // http://localhost:8099/swagger-ui.html
    private final IpDataService ipDataService;

    /**
     * 保存或更新 用户的 IP 数据
     *
     * @param clientIp 客户端 IP
     * @return
     */
    @PostMapping("/ip-statistics")
    @Operation(summary = "保存或更新IP数据")
    public RestResponse<String> saveOrUpdateIpData(@RequestParam("ip") String clientIp, @RequestParam("url") String url) throws Exception {
        ipDataService.saveOrUpdateIpData(clientIp, url);
        return RestResponse.success("IP 数据已保存");
    }

    /**
     * 获取指定时间范围内的 IP 数据
     * 生成并保存按省份统计的柱状图
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    @GetMapping("/ip")
    @Operation(summary = "获取IP统计数据并生成图表")
    public RestResponse<Map<String, String>> getIpData(HttpServletRequest request,
                                                       @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                       @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws Exception {

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);

        // 调用服务生成图表并保存
        String provinceBarChart = ipDataService.generateAndSaveProvinceBarChart(startTime, endTime);
        String wordCloud = ipDataService.generateAndSaveWordCloud(startTime, endTime);
        String chinaHeatMap = ipDataService.generateChinaHeatMap(startTime, endTime);

        // 获取服务器的基础 URL
        String serverUrl = getServerUrl(request);

        // 拼接每个图表的完整 URL
        Map<String, String> chartUrls = new HashMap<>();
        chartUrls.put("柱状图", serverUrl + "/" + provinceBarChart);
        chartUrls.put("词云图", serverUrl + "/" + wordCloud);
        chartUrls.put("热力图", serverUrl + "/" + chinaHeatMap);

        // 返回图表 URLs 的哈希表
        return RestResponse.success(chartUrls);
    }

    private String getServerUrl(HttpServletRequest request) {
        // 获取请求中的协议（http 或 https）
        String scheme = request.getScheme();

        // 获取服务器域名（如 192.168.1.100）或主机名
        String serverName = request.getServerName();

        // 获取请求端口
        int serverPort = request.getServerPort();

        // 拼接完整的服务器 URL
        return scheme + "://" + serverName + ":" + serverPort;
    }

    /**
     * 统计月活跃用户（MAU）
     */
    @GetMapping("/mau")
    @Operation(summary = "获取月活跃用户数")
    public RestResponse<Integer> getMonthlyActiveUsers(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);
        return RestResponse.success(ipDataService.getMonthlyActiveUsers(startTime, endTime));
    }

    /**
     * 统计日活跃用户（DAU）
     */
    @GetMapping("/dau")
    @Operation(summary = "获取日活跃用户数")
    public RestResponse<Integer> getDailyActiveUsers(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return RestResponse.success(ipDataService.getDailyActiveUsers(date.atStartOfDay()));
    }

    /**
     * 计算七日留存率
     */
    @GetMapping("/seven-day-retention")
    @Operation(summary = "获取七日留存率")
    public RestResponse<Double> getSevenDayRetentionRate(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);
        return RestResponse.success(ipDataService.getSevenDayRetentionRate(startTime, endTime));
    }

    /**
     * 获取浏览量（PV）
     */
    @GetMapping("/page-views")
    @Operation(summary = "获取页面浏览量")
    public RestResponse<Integer> getTotalPageViews(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);
        return RestResponse.success(ipDataService.getTotalPageViews(startTime, endTime));
    }

}
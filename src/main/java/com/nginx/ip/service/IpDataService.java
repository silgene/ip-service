package com.nginx.ip.service;

import com.nginx.ip.pojo.entity.IpData;

import java.time.LocalDateTime;
import java.util.List;

public interface IpDataService {
    /**
     * 保存或更新 用户的 IP 数据
     *
     * @param clientIp 客户端 IP
     */
    void saveOrUpdateIpData(String clientIp,String url) throws Exception;

    /**
     * 获取指定时间范围内的 IP 数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    List<IpData> getIpDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成并保存按省份统计的柱状图
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    String generateAndSaveProvinceBarChart(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成并保存词云图
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    String generateAndSaveWordCloud(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成并保存热力图
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @throws Exception
     */
    String generateChinaHeatMap(LocalDateTime startTime, LocalDateTime endTime) throws Exception;

    /**
     * 统计月活跃用户（MAU），即一个月内去重的活跃用户数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 去重后的月活跃用户数
     */
    int getMonthlyActiveUsers(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计日活跃用户（DAU），即当天去重的活跃用户数量
     *
     * @param date 统计日期
     * @return 去重后的日活跃用户数
     */
    int getDailyActiveUsers(LocalDateTime date);

    /**
     * 计算七日留存率（Retention Rate），即 7 天内新用户与老用户的活跃比例
     *
     * @param startTime 起始日期
     * @param endTime   结束日期
     * @return 七日留存率（百分比）
     */
    double getSevenDayRetentionRate(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计浏览量（PV），即时间范围内的所有页面访问次数
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return 总浏览量
     */
    int getTotalPageViews(LocalDateTime startTime, LocalDateTime endTime);

}

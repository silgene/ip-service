package com.nginx.ip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nginx.ip.pojo.entity.IpData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface IpDataMapper extends BaseMapper<IpData> {
    /**
     * 获取指定时间范围内的 IP 数据 （中国）
     */
    @Select("SELECT region_name AS regionName, SUM(access_count) AS accessCount, country AS country " +
            "FROM ip_data " +
            "WHERE date BETWEEN #{startTime} AND #{endTime} AND country = '中国' " +
            "GROUP BY region_name,country " +
            "ORDER BY accessCount DESC")
    List<IpData> getGroupedDataByRegion(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询指定时间范围内的去重月活跃用户数（MAU）
     */
    @Select("SELECT COUNT(DISTINCT ip_address) FROM ip_data WHERE date BETWEEN #{startTime} AND #{endTime}")
    int getMonthlyActiveUsers(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询指定日期的去重日活跃用户数（DAU）
     */
    @Select("SELECT COUNT(DISTINCT ip_address) FROM ip_data WHERE DATE(date) = #{date}")
    int getDailyActiveUsers(@Param("date") LocalDateTime date);

    /**
     * 查询指定时间范围内的新用户和活跃用户数量，用于计算七日留存率
     */
    @Select("SELECT COUNT(DISTINCT ip_address) FROM ip_data WHERE date BETWEEN #{startTime} AND #{endTime}")
    int getSevenDayRetentionBase(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计页面浏览量（PV），即所有页面访问次数
     */
    @Select("SELECT COUNT(*) FROM ip_data WHERE date BETWEEN #{startTime} AND #{endTime}")
    int getTotalPageViews(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}

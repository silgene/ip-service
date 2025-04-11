package com.nginx.ip.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 记录ip当天的聊天次数表
 */
@Data
@TableName("ip_chat")
public class IpChat implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; // 主键

    @TableField("ip_address")
    private String ipAddress; // ip地址

    @TableField("country")
    private String country; // 国家

    @TableField("region_name")
    private String regionName; // 地区

    @TableField("city")
    private String city; // 城市

    @TableField("date")
    private LocalDateTime date; // 日期

    @TableField("update_time")
    private LocalDateTime updateTime; // 更新时间

    @TableField("access_count")
    private Integer accessCount; // 访问次数

    @TableField("url")
    private String url; // 访问的URL
}

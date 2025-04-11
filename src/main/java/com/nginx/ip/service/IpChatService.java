package com.nginx.ip.service;

import java.time.LocalDateTime;

public interface IpChatService {

    /**
     * 保存或更新 用户聊天的 IP 数据
     *
     * @param clientIp 客户端 IP
     */
    void saveOrUpdateIpChat(String clientIp, String url) throws Exception;

    /**
     * 获取指定时间范围内的平均聊天次数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 平均聊天次数
     */
    double getAverageChatCount(LocalDateTime startTime, LocalDateTime endTime);
}

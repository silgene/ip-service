package com.nginx.ip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nginx.ip.pojo.entity.IpChat;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

public interface IpChatMapper extends BaseMapper<IpChat> {
    /**
     * 计算指定时间范围内的每个人的平均聊天次数
     *
     */
    @Select("SELECT AVG(access_count) FROM ip_chat WHERE date BETWEEN #{startTime} AND #{endTime}")
    double getAverageChatCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}

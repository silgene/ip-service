# IP 地理信息分析与可视化模块

## 概述
本模块基于 Spring Boot 框架构建，结合 Nginx 反向代理服务，实现客户端 IP 地址的地理信息分析、数据持久化及可视化展示。系统提供数据统计图表和 RESTful API 接口，适用于访问日志分析、用户地域分布统计等场景。此模块目前只进行`简单实现`，更多需求需要后续拓展。

## 主要功能
- **IP 地理解析**  
  实时解析请求 IP 所属国家/省份/城市
- **数据持久化**  
  将解析结果存储至 MySQL 数据库，支持历史记录追溯
- **可视化展示**  
  生成基于省份的交互式图表：  
  ✅ 地域分布柱状图  
  ✅ 高频访问词云图  
  ✅ 实时流量热力图
- **查询接口**  
  提供按时间范围、地理区域等多条件组合查询 API

## 开发环境
| 组件              | 版本/说明                          |
|-------------------|-----------------------------------|
| 开发框架          | Spring Boot 2.6+                 |
| JDK               | 17                              |
| 数据库            | MySQL 8.0+                       |
| IP 解析服务       | [阿里云IP地理位置查询API](https://market.aliyun.com/apimarket/detail/cmapi00049131?spm=5176.29867242_210807074.0.0.44e83e7eJZfc7O#sku=yuncode43131000010) |


## 代理配置
1. Nginx 1.18+  `IP传递` 示例
   ```nginx
   location / {
       proxy_pass http://localhost:8848; 
       post_action /record_ip; # 上述请求结束后发送新的请求传递ip
   }
   location /record_ip { 
        	internal; # 内部请求
        	proxy_method POST; # POST方法，传递ip
           # 指定解析器，如果是localhost则去掉
           resolver 8.8.8.8 valid=300s; 
           # 动态构造完整 URL
		    set $full_url https://$host$request_uri;
            proxy_pass http://backend/api/ip-statistics?ip=$remote_addr&url=$full_url; 
            proxy_set_header X-Real-IP $remote_addr;
    		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

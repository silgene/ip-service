create database nginx character set utf8mb4 collate utf8mb4_general_ci;

use nginx;

create table ip_chat
(
    id           bigint auto_increment comment '主键'
        primary key,
    ip_address   varchar(45)                           not null comment 'IP 地址',
    country      varchar(30) default ''                null comment '国家',
    region_name  varchar(30) default ''                null comment '地域名/省份',
    city         varchar(30) default ''                null comment '城市',
    date         timestamp   default CURRENT_TIMESTAMP null comment '访问日期',
    access_count int         default 1                 null comment '当天内该 IP 地址的访问次数',
    url          varchar(255)                          not null comment '访问的url',
    update_time  timestamp   default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '聊天表，记录ip当天发送请求次数' charset = utf8mb4;

create table ip_data
(
    id           bigint auto_increment comment '主键'
        primary key,
    ip_address   varchar(45)                           not null comment 'IP 地址',
    country      varchar(30) default ''                null comment '国家',
    region_name  varchar(30) default ''                null comment '地域名/省份',
    city         varchar(30) default ''                null comment '城市',
    date         timestamp   default CURRENT_TIMESTAMP null comment '访问日期',
    access_count int         default 1                 null comment '当天内该 IP 地址的访问次数',
    url          varchar(255)                          not null,
    update_time  timestamp   default CURRENT_TIMESTAMP null comment '更新时间'
)
    charset = utf8mb4;

INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (1, '192.168.121.121', '未知', '未知', '未知', '2025-04-02 16:21:45', 6, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (2, '45.11.1.71', '日本', '东京都', '东京', '2025-04-08 10:07:59', 22, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (3, '8.8.8.8', '美国', '加利福尼亚州', '山景城', '2025-04-08 10:57:46', 456, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (4, '117.71.149.46', '中国', '安徽省', '铜陵', '2025-04-08 11:45:25', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (5, '117.69.236.205', '中国', '安徽省', '淮北', '2025-04-08 11:45:25', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (6, '114.232.109.73', '中国', '江苏省', '南通', '2025-04-08 11:45:26', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (7, '36.6.144.147', '中国', '安徽省', '淮北', '2025-04-08 11:45:26', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (8, '114.231.46.19', '中国', '江苏省', '南通', '2025-04-08 11:45:26', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (9, '114.231.45.50', '中国', '江苏省', '南通', '2025-04-08 11:45:26', 234, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (10, '114.231.46.158', '中国', '江苏省', '南通', '2025-04-08 11:45:26', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (11, '42.63.65.81', '中国', '宁夏回族自治区', '银川', '2025-04-08 11:45:26', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (12, '123.249.123.217', '中国', '北京市', '北京', '2025-04-08 11:45:26', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (13, '113.124.95.158', '中国', '山东省', '烟台', '2025-04-08 11:45:26', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (14, '114.231.45.225', '中国', '江苏省', '南通', '2025-04-08 11:45:26', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (15, '114.231.8.112', '中国', '江苏省', '南通', '2025-04-08 11:45:27', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (16, '183.164.242.40', '中国', '安徽省', '淮北', '2025-04-08 11:45:27', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (17, '112.17.16.242', '中国', '浙江省', '杭州', '2025-04-08 11:45:27', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (18, '113.223.214.63', '中国', '湖南省', '益阳', '2025-04-08 11:45:27', 1, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (19, '114.106.137.220', '中国', '安徽省', '池州', '2025-04-08 11:45:27', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (20, '183.164.243.185', '中国', '安徽省', '淮北', '2025-04-08 11:45:27', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (21, '183.164.243.132', '中国', '安徽省', '淮北', '2025-04-08 11:45:27', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (22, '113.223.213.22', '中国', '湖南省', '益阳', '2025-04-08 11:45:27', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (23, '183.164.243.178', '中国', '安徽省', '淮北', '2025-04-08 11:45:27', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (24, '113.223.212.250', '中国', '湖南省', '益阳', '2025-04-08 11:45:27', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (25, '36.6.144.233', '中国', '安徽省', '淮北', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (26, '183.164.242.181', '中国', '安徽省', '淮北', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (27, '49.71.144.101', '中国', '江苏省', '泰州', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (28, '117.71.149.139', '中国', '安徽省', '铜陵', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (29, '183.164.243.19', '中国', '安徽省', '淮北', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (30, '36.6.145.193', '中国', '安徽省', '淮北', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (31, '114.232.110.67', '中国', '江苏省', '南通', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (32, '117.69.233.123', '中国', '安徽省', '淮北', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (33, '47.97.18.89', '中国', '浙江省', '杭州', '2025-04-08 11:45:28', 2, '', '2025-04-11 01:44:59');
INSERT INTO nginx.ip_data (id, ip_address, country, region_name, city, date, access_count, url, update_time) VALUES (34, '114.232.110.245', '中国', '江苏省', '南通', '2025-04-08 11:45:29', 3, '', '2025-04-11 01:44:59');

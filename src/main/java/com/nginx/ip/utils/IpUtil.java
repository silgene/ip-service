package com.nginx.ip.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @decription IP工具类，这里用的是 MaxMind 的 GeoLite2 数据库
 * 本项目中目前-----暂不考虑-----使用这个，留作后路
 * 使用前注意先下载最新的mmdb文件替换旧的mmdb文件（MaxMind官网）
 * 使用GeoLite2时，注意定期写脚本更新数据库，防止数据过旧导致ip分析不准确
 */
public class IpUtil {
    // 缓存 DatabaseReader，避免重复加载
    private static DatabaseReader reader;

    // 静态代码块加载 GeoLite2 数据库
    static {
        try (InputStream inputStream = IpUtil.class.getClassLoader().getResourceAsStream("GeoLite2-City.mmdb")) {
            // 创建临时文件
            File tempFile = File.createTempFile("geolite2-city", ".mmdb");
            tempFile.deleteOnExit();

            // 将资源流复制到临时文件
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 构建 DatabaseReader
            reader = new DatabaseReader.Builder(tempFile).build();
        } catch (Exception e) {
            throw new RuntimeException("GeoLite2数据库加载失败", e);
        }
    }

    /**
     * 根据IP地址获取国家、省份和城市
     *
     * @param ip IP地址
     * @return 国家、省份和城市名称
     */
    public static String[] getIpLocation(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = reader.city(ipAddress);

            // 获取多语言名称
            Country country = response.getCountry();
            Subdivision subdivision = response.getMostSpecificSubdivision();
            City city = response.getCity();

            String countryName = country.getNames().getOrDefault("zh-CN", "未知");
            String regionName = subdivision.getNames().getOrDefault("zh-CN", "未知");
            String cityName = city.getNames().getOrDefault("zh-CN", "未知");

            return new String[]{countryName, regionName, cityName};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"未知", "未知", "未知"};
        }
    }


    public static void main(String[] args) {
        // 使用示例，根据需要自行修改上述代码
        try {
            InetAddress ipAddress = InetAddress.getByName("223.104.87.172");
            CityResponse response = reader.city(ipAddress);

            // 打印所有数据
            System.out.println("IP地址: " + ipAddress.getHostAddress());
            System.out.println("国家: " + response.getCountry().getNames().getOrDefault("zh-CN", "未知"));
            System.out.println("地区: " + response.getMostSpecificSubdivision().getNames().getOrDefault("zh-CN", "未知"));
            System.out.println("城市: " + response.getCity().getNames().getOrDefault("zh-CN", "未知"));
            System.out.println("邮政编码: " + response.getPostal().getCode());
            System.out.println("时区: " + response.getLocation().getTimeZone());
            System.out.println("经度: " + response.getLocation().getLongitude());
            System.out.println("纬度: " + response.getLocation().getLatitude());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查询失败");
        }
    }

}
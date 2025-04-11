package com.nginx.ip.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @description 常量类，用于存储特殊区域和全称的映射关系
 */
public class RegionConstants {
    public static final Map<String, String> REGION_MAPPING = new HashMap<>();

    static {
        REGION_MAPPING.put("北京", "北京市");
        REGION_MAPPING.put("上海", "上海市");
        REGION_MAPPING.put("天津", "天津市");
        REGION_MAPPING.put("重庆", "重庆市");
        REGION_MAPPING.put("新疆", "新疆维吾尔自治区");
        REGION_MAPPING.put("广西", "广西壮族自治区");
        REGION_MAPPING.put("宁夏", "宁夏回族自治区");
        REGION_MAPPING.put("内蒙古", "内蒙古自治区");
        REGION_MAPPING.put("西藏", "西藏自治区");
        REGION_MAPPING.put("香港", "香港特别行政区");
        REGION_MAPPING.put("澳门", "澳门特别行政区");
    }
}


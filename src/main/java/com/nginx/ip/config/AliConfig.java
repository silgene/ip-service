package com.nginx.ip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "ali")
public class AliConfig {
    private String appcode;
    private String url;
}

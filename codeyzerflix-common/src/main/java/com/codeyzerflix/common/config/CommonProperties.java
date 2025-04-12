package com.codeyzerflix.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "codeyzerflix-common")
public class CommonProperties {
    private CommonCorsProperties cors = new CommonCorsProperties();
} 
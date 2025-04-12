package com.codeyzerflix.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "codeyzerflix-admin")
public class AdminProperties {
    private List<String> allowedVideoTypes;
} 
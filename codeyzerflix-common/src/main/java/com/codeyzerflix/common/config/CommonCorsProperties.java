package com.codeyzerflix.common.config;

import java.util.List;

import lombok.Data;

@Data
public class CommonCorsProperties {
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private boolean allowCredentials;
    private long maxAge;
} 
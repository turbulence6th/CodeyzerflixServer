package com.codeyzerflix.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.codeyzerflix.admin", "com.codeyzerflix.common"})
@EnableScheduling
public class CodeyzerFlixAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeyzerFlixAdminApplication.class, args);
    }
} 
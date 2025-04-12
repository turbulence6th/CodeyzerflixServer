package com.codeyzerflix.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.codeyzerflix.api", "com.codeyzerflix.common"})
public class CodeyzerFlixApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeyzerFlixApiApplication.class, args);
    }
} 
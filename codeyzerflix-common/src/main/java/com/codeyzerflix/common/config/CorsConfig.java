package com.codeyzerflix.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final CommonProperties commonProperties;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // İzin verilen originleri ekle
        commonProperties.getCors().getAllowedOrigins().forEach(config::addAllowedOrigin);
        
        // İzin verilen metodları ekle
        commonProperties.getCors().getAllowedMethods().forEach(config::addAllowedMethod);
        
        // İzin verilen headerları ekle
        commonProperties.getCors().getAllowedHeaders().forEach(config::addAllowedHeader);
        
        // Credentials ayarı
        config.setAllowCredentials(commonProperties.getCors().isAllowCredentials());
        
        // Preflight isteklerinin önbellek süresi
        config.setMaxAge(commonProperties.getCors().getMaxAge());
        
        // Tüm path'ler için bu yapılandırmayı uygula
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
} 
package com.dzh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author DongZH
 * @date 2023/4/11 15:32
 * @text 解决跨域问题
 */
@Configuration
public class MyCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1.添加CORS配置信息
        CorsConfiguration configuration = new CorsConfiguration();
        // 1).允许的域，不要写*，否则cookie就无法使用了
        configuration.addAllowedOrigin("http://localhost:8888");   // 这里填写请求的前端服务器
        // 是否发送Cookie信息
        configuration.setAllowCredentials(true);
        // 允许的请求方式
//        configuration.addAllowedMethod("*");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("HEAD");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        // 允许的头信息
        configuration.addAllowedHeader("*");

        // 2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);

        // 3.返回新的CorsFilter
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}

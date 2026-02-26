package com.example.travelserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 映射 uploads 文件夹 (用户上传)
        // 注意：路径末尾必须有 /
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/bishe_files/uploads/");

        // 2. 映射 assets 文件夹 (系统预置)
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:D:/bishe_files/assets/");
    }
}
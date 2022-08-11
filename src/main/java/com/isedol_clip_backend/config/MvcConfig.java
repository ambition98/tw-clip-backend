package com.isedol_clip_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                    .allowedOrigins("http://localhost:8080", "https://isedol-clip.xyz")
                    .allowedHeaders("Authorization", "Content-Type", "access-control-allow-origin")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowCredentials(true);
    }
}

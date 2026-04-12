package com.tammam.roomatedash.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MockAuthInterceptor mockAuthInterceptor;

    public WebConfig(MockAuthInterceptor mockAuthInterceptor) {
        this.mockAuthInterceptor = mockAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mockAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/dashboard.css",
                        "/error",
                        "/h2/**"
                );
    }
}

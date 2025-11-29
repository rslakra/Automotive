package com.rslakra.automobile.config;

import com.rslakra.automobile.service.security.interceptor.LoggerInterceptor;
import com.rslakra.automobile.service.security.interceptor.SessionTimerInterceptor;
import com.rslakra.automobile.service.security.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    public WebMvcConfig() {
        super();
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor())
            .excludePathPatterns("/h2/**");
        registry.addInterceptor(new UserInterceptor())
            .excludePathPatterns("/h2/**");
        registry.addInterceptor(new SessionTimerInterceptor())
            .excludePathPatterns("/h2/**");
    }
}


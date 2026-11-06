package com.cafuc.novelapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;

    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login",
                        "/bookshelf/**",
                        "/user/register",
                        "/user/**",
                        "/category/**",
                        "/book/**",
                        "/swagger-ui/**",
                        "/covers/**",
                        "/announcement/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/chapter/**",
                        "/readingProgress/**",
                        "/webjars/**",
                        "/doc.html",
                        "/admin/**",
                        "/upload/**",
                        "/favicon.ico");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .maxAge(3600);
    }
}

package com.cafuc.novelapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@MapperScan("com.cafuc.novelapp.mapper")
@SpringBootApplication
public class NovelAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelAppApplication.class, args);
    }

}

package com.minimall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.minimall.mapper")
public class MiniMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniMallApplication.class, args);
    }
}
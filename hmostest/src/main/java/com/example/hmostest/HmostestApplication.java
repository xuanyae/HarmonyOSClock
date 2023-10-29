package com.example.hmostest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.hmostest.dao")
public class HmostestApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmostestApplication.class, args);
    }

}

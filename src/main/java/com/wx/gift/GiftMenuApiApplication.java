package com.wx.gift;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wx.gift.mapper")
public class GiftMenuApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(GiftMenuApiApplication.class, args);
    }
}


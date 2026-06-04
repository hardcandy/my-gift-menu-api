package com.wx.gift.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {
    @Value("${okhttp.config.connectTimeout:10}")
    private Integer connectTimeout;
    @Value("${okhttp.config.writeTimeout:5}")
    private Integer writeTimeout;
    @Value("${okhttp.config.readTimeout:10}")
    private Integer readTimeout;
    @Value("${okhttp.config.maxIdleConnections:5}")
    private Integer maxIdleConnections;
    @Value("${okhttp.config.keepAliveDuration:5}")
    private Long keepAliveDuration;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES))
                .build();
    }
}


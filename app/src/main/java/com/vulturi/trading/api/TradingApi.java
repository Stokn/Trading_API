package com.vulturi.trading.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication(scanBasePackages = {"com.vulturi.trading.api"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableJpaRepositories
@EnableScheduling
@EnableFeignClients
public class TradingApi {
    public static void main(String[] args) {
        SpringApplication.run(TradingApi.class);
    }
}
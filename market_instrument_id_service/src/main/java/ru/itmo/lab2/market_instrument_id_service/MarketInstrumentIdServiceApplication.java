package ru.itmo.lab2.market_instrument_id_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableReactiveFeignClients
@SpringBootApplication
@EnableEurekaClient
@EnableR2dbcRepositories
@EnableHystrix
public class MarketInstrumentIdServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketInstrumentIdServiceApplication.class, args);
    }

}

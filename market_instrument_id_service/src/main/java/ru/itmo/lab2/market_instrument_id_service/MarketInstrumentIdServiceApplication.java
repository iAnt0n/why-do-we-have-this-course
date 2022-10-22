package ru.itmo.lab2.market_instrument_id_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MarketInstrumentIdServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketInstrumentIdServiceApplication.class, args);
    }

}

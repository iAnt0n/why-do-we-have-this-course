package ru.itmo.lab1.instrument_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class InstrumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstrumentServiceApplication.class, args);
    }

}

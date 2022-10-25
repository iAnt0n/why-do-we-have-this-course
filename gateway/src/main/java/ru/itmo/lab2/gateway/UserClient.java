package ru.itmo.lab2.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.gateway.model.dto.UserDto;

import java.util.UUID;

@ReactiveFeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/{id}")
    Mono<UserDto> findById(@PathVariable("id") UUID id);
}

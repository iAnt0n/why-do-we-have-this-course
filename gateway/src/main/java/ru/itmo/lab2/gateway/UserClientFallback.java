package ru.itmo.lab2.gateway;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.gateway.model.dto.UserDto;

import java.util.UUID;

@Component
public class UserClientFallback implements UserClient{
    @Override
    public Mono<UserDto> findById(UUID id) {
        return Mono.empty();
    }
}

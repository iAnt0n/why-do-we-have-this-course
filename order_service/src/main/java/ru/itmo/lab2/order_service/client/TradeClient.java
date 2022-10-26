package ru.itmo.lab2.order_service.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.order_service.model.dto.TradeDto;

@ReactiveFeignClient(name = "TRADE-SERVICE", fallback = TradeClientFallback.class)
public interface TradeClient {
    @PostMapping("/trade")
    Mono<TradeDto> createTrade(@RequestBody TradeDto trade);
}

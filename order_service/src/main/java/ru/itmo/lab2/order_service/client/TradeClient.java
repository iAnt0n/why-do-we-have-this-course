package ru.itmo.lab2.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.itmo.lab2.order_service.model.dto.TradeDto;

@FeignClient(name = "TRADE-SERVICE", fallback = TradeClientFallback.class)
public interface TradeClient {
    @PostMapping("/trade/internal")
    TradeDto createTrade(@RequestBody TradeDto trade);
}

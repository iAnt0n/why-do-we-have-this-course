package ru.itmo.lab2.order_service.client;

import org.springframework.stereotype.Component;
import ru.itmo.lab2.order_service.model.dto.TradeDto;

@Component
public class TradeClientFallback implements TradeClient {
    @Override
    public TradeDto createTrade(TradeDto trade) {
        return null;
    }
}

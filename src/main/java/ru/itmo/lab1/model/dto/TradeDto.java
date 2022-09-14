package ru.itmo.lab1.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TradeDto implements Serializable {
    private UUID id;
    private MarketInstrumentIdDto idMiid;
    private UUID idOrderId;
    private UUID idPortfolioId;
    private Integer volume;
    private Double price;
    private Instant createdDatetime;
}

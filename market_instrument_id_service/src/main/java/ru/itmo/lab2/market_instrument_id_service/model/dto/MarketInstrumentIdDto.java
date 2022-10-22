package ru.itmo.lab2.market_instrument_id_service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MarketInstrumentIdDto implements Serializable {
    private UUID id;
    private UUID idMarketId;
    private UUID idInstrumentId;
    private String currency;
    private Boolean deleted;
}

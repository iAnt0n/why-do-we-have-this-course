package ru.itmo.lab2.market_instrument_id_service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab2.market_instrument_id_service.model.enums.MIC;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MarketDto implements Serializable {
    private UUID id;
    private MIC mic;
    private String location;
}

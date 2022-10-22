package ru.itmo.lab2.market_instrument_id_service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab2.market_instrument_id_service.model.enums.InstrumentStatus;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class InstrumentDto implements Serializable {
    private UUID id;
    private String isin;
    private InstrumentStatus status;
}

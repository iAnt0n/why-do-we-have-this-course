package ru.itmo.lab2.market_instrument_id_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab2.market_instrument_id_service.model.enums.InstrumentStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentPatchDto implements Serializable {
    private InstrumentStatus status;
}

package ru.itmo.lab1.instrument_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab1.instrument_service.model.enums.InstrumentStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentPatchDto implements Serializable {
    private InstrumentStatus status;
}

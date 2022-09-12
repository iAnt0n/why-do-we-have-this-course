package ru.itmo.lab1.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class InstrumentDto implements Serializable {
    private UUID id;
    private String isin;
    private String status;
}

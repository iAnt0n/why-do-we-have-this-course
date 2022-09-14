package ru.itmo.lab1.model.dto;

import lombok.*;
import ru.itmo.lab1.model.MIC;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MarketDto implements Serializable {
    private UUID id;
    private MIC mic;
    private String location;
}

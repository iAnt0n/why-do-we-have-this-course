package ru.itmo.lab2.market_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDto implements Serializable {
    private UUID id;
    private MIC mic;
    private String location;
}

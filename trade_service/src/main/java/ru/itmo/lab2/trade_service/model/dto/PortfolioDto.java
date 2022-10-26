package ru.itmo.lab2.trade_service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PortfolioDto implements Serializable {
    private UUID id;
    private UUID idUserId;
    private String name;
    private Set<UUID> trades = new HashSet<>();
}

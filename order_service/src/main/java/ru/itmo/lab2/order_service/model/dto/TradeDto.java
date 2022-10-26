package ru.itmo.lab2.order_service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TradeDto implements Serializable {
    private UUID id;
    private UUID idMiid;
    private UUID idUser;
    private UUID idOrder;
    private UUID idPortfolio;
    private Integer volume;
    private Double price;
    private Instant createdDatetime;
}

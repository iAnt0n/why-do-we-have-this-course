package ru.itmo.lab1.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderDto implements Serializable {
    private UUID id;
    private UUID idMiidId;
    private String status;
    private String orderType;
    private Integer volume;
    private Double price;
    private Instant createdDatetime;
}

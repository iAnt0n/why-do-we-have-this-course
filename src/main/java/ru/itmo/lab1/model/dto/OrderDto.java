package ru.itmo.lab1.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab1.model.enums.OrderStatus;
import ru.itmo.lab1.model.enums.OrderType;
import ru.itmo.lab1.model.enums.Side;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderDto implements Serializable {
    private UUID id;
    private UUID idMiidId;
    private UUID idUserId;
    private OrderStatus status;
    private OrderType orderType;
    private Integer volume;
    private Double price;
    private Instant createdDatetime;
    private Side side;
}

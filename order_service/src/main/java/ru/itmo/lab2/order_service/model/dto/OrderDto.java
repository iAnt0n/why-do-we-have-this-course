package ru.itmo.lab2.order_service.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab2.order_service.model.enums.OrderStatus;
import ru.itmo.lab2.order_service.model.enums.OrderType;
import ru.itmo.lab2.order_service.model.enums.Side;

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

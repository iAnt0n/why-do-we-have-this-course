package ru.itmo.lab2.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.lab2.order_service.model.enums.OrderStatus;
import ru.itmo.lab2.order_service.model.enums.OrderType;
import ru.itmo.lab2.order_service.model.enums.Side;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "id_miid")
    private UUID idMiid;

    @Column(name = "id_user", nullable = false)
    private UUID idUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @Column(name = "volume", nullable = false)
    private Integer volume;

    @Column(name = "price", nullable = false)
    private Double price;
    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private Side side;
}

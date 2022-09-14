package ru.itmo.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.lab1.model.enums.OrderStatus;
import ru.itmo.lab1.model.enums.OrderType;
import ru.itmo.lab1.model.enums.Side;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_miid")
    private MarketInstrumentId idMiid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User idUser;

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

    @OneToMany(mappedBy = "idOrder")
    private Set<Trade> trades = new LinkedHashSet<>();

    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private Side side;
}

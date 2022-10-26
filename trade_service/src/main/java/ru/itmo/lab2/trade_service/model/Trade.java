package ru.itmo.lab2.trade_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "trade")
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "id_miid")
    private UUID idMiid;

    @Column(name = "id_user", nullable = false)
    private UUID idUser;

    @Column(name = "id_order")
    private UUID idOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_portfolio")
    private Portfolio idPortfolio;

    @Column(name = "volume", nullable = false)
    private Integer volume;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;
}

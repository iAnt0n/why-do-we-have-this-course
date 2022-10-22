package ru.itmo.lab2.market_instrument_id_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "market_instrument_id")
public class MarketInstrumentId {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "id_market")
    private UUID idMarket;

    @Column(name = "id_instrument")
    private UUID idInstrument;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
}

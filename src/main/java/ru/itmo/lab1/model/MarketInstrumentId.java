package ru.itmo.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_market")
    private Market idMarket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instrument")
    private Instrument idInstrument;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @OneToMany(mappedBy = "idMiid")
    private Set<Trade> trades = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idMiid")
    private Set<Order> orders = new LinkedHashSet<>();
}

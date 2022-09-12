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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "mic")
    private MIC mic;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "idMarket")
    private Set<MarketInstrumentId> marketInstruments = new LinkedHashSet<>();
}

package ru.itmo.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.lab1.model.enums.InstrumentStatus;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "instrument")
public class Instrument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "isin", nullable = false, length = 12)
    private String isin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InstrumentStatus status;

    @OneToMany(mappedBy = "idInstrument")
    private Set<MarketInstrumentId> marketInstruments = new LinkedHashSet<>();
}

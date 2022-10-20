package ru.itmo.lab1.instrument_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.lab1.instrument_service.model.enums.InstrumentStatus;

import javax.persistence.*;
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
}

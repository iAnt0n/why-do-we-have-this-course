package ru.itmo.lab2.market_instrument_id_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "market_instrument_id")
public class MarketInstrumentId implements Persistable<UUID> {
    @Id
    @Column("id")
    private UUID id;

    @Column("id_market")
    private UUID idMarket;

    @Column("id_instrument")
    private UUID idInstrument;

    @Column("currency")
    private String currency;

    @Column("deleted")
    private Boolean deleted = false;

    @Override
    public boolean isNew() {
        boolean isNew = (this.id == null);
        if (isNew) {
            this.setId(UUID.randomUUID());
        }
        return isNew;
    }
}

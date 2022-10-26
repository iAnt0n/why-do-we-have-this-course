package ru.itmo.lab2.trade_service.model;

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
@Table(name = "portfolio")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "id_user")
    private UUID idUser;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "idPortfolio", cascade = {CascadeType.PERSIST})
    private Set<Trade> trades = new LinkedHashSet<>();

    @PreRemove
    private void preRemove() {
        trades.forEach(child -> child.setIdPortfolio(null));
    }
}

package ru.itmo.lab1.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.model.Trade;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, UUID> {
    List<Trade> findByIdIn(Collection<UUID> ids);

    Page<Trade> findAllByIdUserId(UUID id, Pageable pageable);
}
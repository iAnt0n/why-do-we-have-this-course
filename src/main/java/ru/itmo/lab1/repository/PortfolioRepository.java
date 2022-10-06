package ru.itmo.lab1.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.lab1.model.Portfolio;

import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    Page<Portfolio> findAllByIdUserId(UUID id, Pageable pageable);
}
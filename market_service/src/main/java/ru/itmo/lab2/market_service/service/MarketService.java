package ru.itmo.lab2.market_service.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.lab2.market_service.controller.exception.MarketNotFoundException;
import ru.itmo.lab2.market_service.model.MarketDto;
import ru.itmo.lab2.market_service.repository.MarketRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MarketService {
    private MarketRepository marketRepository;

    public Page<MarketDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return marketRepository.findAll(pageable);
    }

    public MarketDto findById(UUID id) {
        return marketRepository.findById(id).orElseThrow(
                () -> new MarketNotFoundException(id)
        );
    }

    public MarketDto create(MarketDto marketDTO) {
        marketDTO.setId(UUID.randomUUID());
        int rc = marketRepository.save(marketDTO);
        if (rc != 1) {
            throw new RuntimeException("Could not add " + marketDTO);
        }
        return marketDTO;
    }
}

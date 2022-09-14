package ru.itmo.lab1.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.lab1.controller.exception.MarketNotFoundException;
import ru.itmo.lab1.model.Market;
import ru.itmo.lab1.model.dto.MarketDto;
import ru.itmo.lab1.repository.MarketRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MarketService {
    private MarketRepository marketRepository;
    private ModelMapper modelMapper;

    public Page<MarketDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return marketRepository.findAll(pageable).map(market -> modelMapper.map(market, MarketDto.class));
    }

    public MarketDto findById(UUID id) {
        return marketRepository.findById(id).map(market -> modelMapper.map(market, MarketDto.class)).orElseThrow(
                () -> new MarketNotFoundException(id)
        );
    }

    public MarketDto create(MarketDto marketDTO) {
        Market market = modelMapper.map(marketDTO, Market.class);
        market.setId(UUID.randomUUID());
        return modelMapper.map(marketRepository.save(market), MarketDto.class);
    }
}

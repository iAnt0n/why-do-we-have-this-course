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

import java.util.Optional;
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

    public MarketDto save(MarketDto marketDTO) {
        Market market = modelMapper.map(marketDTO, Market.class);
        market.setId(UUID.randomUUID());
        return modelMapper.map(marketRepository.save(market), MarketDto.class);
    }

    public void delete(UUID id) {
        Market market = marketRepository.findById(id).orElseThrow(() -> new MarketNotFoundException(id));
        marketRepository.delete(market);
    }

    public MarketDto modify(MarketDto marketDto) {
        if (!marketRepository.existsById(marketDto.getId())) {
            throw new MarketNotFoundException(marketDto.getId());
        }
        Market market = marketRepository.save(modelMapper.map(marketDto, Market.class));
        return modelMapper.map(market, MarketDto.class);
    }
}

package ru.itmo.lab1.service;

import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.lab1.controller.exception.PortfolioNotFoundException;
import ru.itmo.lab1.model.Portfolio;
import ru.itmo.lab1.model.Trade;
import ru.itmo.lab1.model.dto.PortfolioDto;
import ru.itmo.lab1.model.dto.PortfolioPatchDto;
import ru.itmo.lab1.repository.PortfolioRepository;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfolioService {
    private PortfolioRepository portfolioRepository;
    private TradeService tradeService;
    private ModelMapper modelMapper;

    @PostConstruct
    void init() {
        TypeMap<Portfolio, PortfolioDto> tm = modelMapper.createTypeMap(Portfolio.class, PortfolioDto.class);
        Converter<Set<Trade>, Set<UUID>> tradeToIds = c -> c.getSource().stream().map(Trade::getId).collect(Collectors.toSet());
        tm.addMappings(
                mapper -> mapper.using(tradeToIds).map(Portfolio::getTrades, PortfolioDto::setTrades)
        );
    }

    public Page<PortfolioDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return portfolioRepository.findAll(pageable).map(portfolio -> modelMapper.map(portfolio, PortfolioDto.class));
    }

    public PortfolioDto findById(UUID id) {
        return portfolioRepository.findById(id).map(portfolio -> modelMapper.map(portfolio, PortfolioDto.class)).orElseThrow(
                () -> new PortfolioNotFoundException(id)
        );
    }

    public PortfolioDto create(PortfolioDto portfolioDTO) {
        Portfolio portfolio = modelMapper.map(portfolioDTO, Portfolio.class);
        portfolio.setId(UUID.randomUUID());
        return modelMapper.map(portfolioRepository.save(portfolio), PortfolioDto.class);
    }

    public void delete(UUID id) {
        portfolioRepository.deleteById(id);
    }

    private void rename(Portfolio portfolio, String name) {
        portfolio.setName(name);
        portfolioRepository.save(portfolio);
    }

    private void updateTrades(Portfolio portfolio, Collection<UUID> tradeIds) {
        Set<UUID> curIds = portfolio.getTrades().stream().map(Trade::getId).collect(Collectors.toSet());
        Set<UUID> newIds = new HashSet<>(tradeIds);

        Set<UUID> removeSet = curIds.stream().filter(e -> !newIds.contains(e)).collect(Collectors.toSet());
        Set<UUID> addSet = newIds.stream().filter(e -> !curIds.contains(e)).collect(Collectors.toSet());

        for (UUID tradeId : removeSet) {
            tradeService.setPortfolio(tradeId, Optional.empty());
        }
        for (UUID tradeId : addSet) {
            tradeService.setPortfolio(tradeId, Optional.of(portfolio));
        }
    }

    @Transactional
    public PortfolioDto update(UUID id, PortfolioPatchDto dto) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id));

        if (dto.getName() != null) {
            rename(portfolio, dto.getName());
        }
        if (dto.getTrades() != null) {
            updateTrades(portfolio, dto.getTrades());
        }
        portfolio = portfolioRepository.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id));
        return modelMapper.map(portfolio, PortfolioDto.class);
    }
}

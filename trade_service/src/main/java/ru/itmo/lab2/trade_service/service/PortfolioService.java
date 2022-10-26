package ru.itmo.lab2.trade_service.service;

import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.itmo.lab2.trade_service.controller.exception.PortfolioNotFoundException;
import ru.itmo.lab2.trade_service.model.Portfolio;
import ru.itmo.lab2.trade_service.model.Trade;
import ru.itmo.lab2.trade_service.model.dto.PortfolioDto;
import ru.itmo.lab2.trade_service.model.dto.PortfolioPatchDto;
import ru.itmo.lab2.trade_service.repository.PortfolioRepository;

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

    public Mono<Page<PortfolioDto>> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> portfolioRepository
                        .findAll(pageable).map(portfolio -> modelMapper.map(portfolio, PortfolioDto.class)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Page<PortfolioDto>> findAllByIdUser(UUID id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> portfolioRepository.findAllByIdUser(id, pageable)
                        .map(portfolio -> modelMapper.map(portfolio, PortfolioDto.class)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<PortfolioDto> findById(UUID id) {
        return Mono.fromCallable(() -> portfolioRepository.findById(id)
                        .map(portfolio -> modelMapper.map(portfolio, PortfolioDto.class))
                        .orElseThrow(() -> new PortfolioNotFoundException(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<PortfolioDto> create(PortfolioDto portfolioDTO) {
        Portfolio portfolio = modelMapper.map(portfolioDTO, Portfolio.class);
        portfolio.setId(UUID.randomUUID());
        return Mono.fromCallable(() -> modelMapper.map(portfolioRepository.save(portfolio), PortfolioDto.class))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Object> delete(UUID id) {
        return Mono.fromCallable(() -> {
                    portfolioRepository.deleteById(id);
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic());
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
            tradeService.setPortfolio(tradeId, Optional.empty()).block();
        }
        for (UUID tradeId : addSet) {
            tradeService.setPortfolio(tradeId, Optional.of(portfolio)).block();
        }
    }

    @Transactional
    public Mono<PortfolioDto> update(UUID id, PortfolioPatchDto dto) {
        return Mono.fromCallable(() -> {
            Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id));

            if (dto.getName() != null) {
                rename(portfolio, dto.getName());
            }
            if (dto.getTrades() != null) {
                updateTrades(portfolio, dto.getTrades());
            }
            portfolio = portfolioRepository.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id));
            return modelMapper.map(portfolio, PortfolioDto.class);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

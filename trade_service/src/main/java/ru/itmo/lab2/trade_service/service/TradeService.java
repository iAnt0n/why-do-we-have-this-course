package ru.itmo.lab2.trade_service.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.itmo.lab2.trade_service.controller.exception.TradeManualCreationException;
import ru.itmo.lab2.trade_service.controller.exception.TradeNotFoundException;
import ru.itmo.lab2.trade_service.model.Portfolio;
import ru.itmo.lab2.trade_service.model.Trade;
import ru.itmo.lab2.trade_service.model.dto.TradeDto;
import ru.itmo.lab2.trade_service.repository.TradeRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TradeService {
    private TradeRepository tradeRepository;
    private ModelMapper modelMapper;

    public Mono<Page<TradeDto>> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(() -> tradeRepository.findAll(pageable)
                .map(trade -> modelMapper.map(trade, TradeDto.class)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<TradeDto> findById(UUID id) {
        return Mono.fromCallable(() ->tradeRepository.findById(id).map(trade -> modelMapper.map(trade, TradeDto.class))
                .orElseThrow(() -> new TradeNotFoundException(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Page<TradeDto>> findAllByIdUser(UUID id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Mono.fromCallable(()->tradeRepository.findAllByIdUser(id, pageable)
                .map(trade -> modelMapper.map(trade, TradeDto.class)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<TradeDto> createExternal(TradeDto dto) {
        return Mono.fromCallable(() -> {
            if (dto.getIdPortfolioId() != null || dto.getIdOrderId() != null) {
                throw new TradeManualCreationException();
            }
            return create(dto);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public TradeDto create(TradeDto dto) {
        Trade trade = modelMapper.map(dto, Trade.class);
        trade.setId(UUID.randomUUID());
        if (trade.getCreatedDatetime() == null) {
            trade.setCreatedDatetime(Instant.now());
        }
        return modelMapper.map(tradeRepository.save(trade), TradeDto.class);
    }

    public Mono<Object> setPortfolio(UUID tradeId, Optional<Portfolio> portfolio) {
        return Mono.fromCallable(()-> {
            Trade trade = tradeRepository.findById(tradeId).orElseThrow(() -> new TradeNotFoundException(tradeId));
            trade.setIdPortfolio(portfolio.orElse(null));
            trade = tradeRepository.save(trade);
            tradeRepository.save(trade);
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

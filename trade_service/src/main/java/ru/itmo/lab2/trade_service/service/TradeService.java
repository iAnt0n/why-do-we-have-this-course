package ru.itmo.lab2.trade_service.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    public Page<TradeDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tradeRepository.findAll(pageable).map(trade -> modelMapper.map(trade, TradeDto.class));
    }

    public TradeDto findById(UUID id) {
        return tradeRepository.findById(id).map(trade -> modelMapper.map(trade, TradeDto.class)).orElseThrow(
                () -> new TradeNotFoundException(id)
        );
    }

    public Page<TradeDto> findAllByIdUser(UUID id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tradeRepository.findAllByIdUser(id, pageable).map(trade -> modelMapper.map(trade, TradeDto.class));
    }

    public TradeDto createExternal(TradeDto dto) {
        if (dto.getIdPortfolioId() != null || dto.getIdOrderId() != null) {
            throw new TradeManualCreationException();
        }
        return create(dto);
    }

    public TradeDto create(TradeDto dto) {
        Trade trade = modelMapper.map(dto, Trade.class);
        trade.setId(UUID.randomUUID());
        if (trade.getCreatedDatetime() == null) {
            trade.setCreatedDatetime(Instant.now());
        }
        return modelMapper.map(tradeRepository.save(trade), TradeDto.class);
    }

    public void setPortfolio(UUID tradeId, Optional<Portfolio> portfolio) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() -> new TradeNotFoundException(tradeId));
        trade.setIdPortfolio(portfolio.orElse(null));
        trade = tradeRepository.save(trade);
        tradeRepository.save(trade);
    }
}

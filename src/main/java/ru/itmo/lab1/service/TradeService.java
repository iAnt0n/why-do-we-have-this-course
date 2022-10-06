package ru.itmo.lab1.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.lab1.controller.exception.TradeManualCreationException;
import ru.itmo.lab1.controller.exception.TradeNotFoundException;
import ru.itmo.lab1.model.Portfolio;
import ru.itmo.lab1.model.Trade;
import ru.itmo.lab1.model.dto.TradeDto;
import ru.itmo.lab1.repository.TradeRepository;

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
        return tradeRepository.findAllByIdUserId(id, pageable).map(trade -> modelMapper.map(trade, TradeDto.class));
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
        if (trade.getCreatedDatetime()==null) {
            trade.setCreatedDatetime(Instant.now());
        }
        return modelMapper.map(tradeRepository.save(trade), TradeDto.class);
    }

    public TradeDto setPortfolio(UUID trade_id, Optional<Portfolio> portfolio) {
        Trade trade = tradeRepository.findById(trade_id).orElseThrow(() -> new TradeNotFoundException(trade_id));
        trade.setIdPortfolio(portfolio.orElse(null));
        trade = tradeRepository.save(trade);
        return modelMapper.map(tradeRepository.save(trade), TradeDto.class);
    }
}

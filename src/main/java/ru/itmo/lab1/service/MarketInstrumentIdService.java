package ru.itmo.lab1.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.lab1.controller.exception.MarketInstrumentIdNotFoundException;
import ru.itmo.lab1.model.Instrument;
import ru.itmo.lab1.model.MarketInstrumentId;
import ru.itmo.lab1.model.dto.MarketInstrumentIdDto;
import ru.itmo.lab1.model.enums.InstrumentStatus;
import ru.itmo.lab1.repository.MarketInstrumentIdRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MarketInstrumentIdService {
    private MarketInstrumentIdRepository marketInstrumentIdRepository;
    private InstrumentService instrumentService;
    private ModelMapper modelMapper;

    public Page<MarketInstrumentIdDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return marketInstrumentIdRepository.findAll(pageable).map(marketInstrumentId -> modelMapper.map(marketInstrumentId, MarketInstrumentIdDto.class));
    }

    public MarketInstrumentIdDto findById(UUID id) {
        return marketInstrumentIdRepository.findById(id).map(miid -> modelMapper.map(miid, MarketInstrumentIdDto.class)).orElseThrow(
                () -> new MarketInstrumentIdNotFoundException(id)
        );
    }

    @Transactional
    public MarketInstrumentIdDto create(MarketInstrumentIdDto dto) {
        MarketInstrumentId marketInstrumentId = modelMapper.map(dto, MarketInstrumentId.class);
        marketInstrumentId.setId(UUID.randomUUID());

        Instrument instrument = marketInstrumentId.getIdInstrument();
        instrumentService.changeStatus(instrument.getId(),
                instrument.getStatus() == InstrumentStatus.DELISTED
                        ? InstrumentStatus.SINGLELISTED
                        : InstrumentStatus.MULTILISTED);

        marketInstrumentId = marketInstrumentIdRepository.save(marketInstrumentId);

        return modelMapper.map(marketInstrumentId, MarketInstrumentIdDto.class);
    }

    @Transactional
    public void delete(UUID id) {
        MarketInstrumentId marketInstrumentId = marketInstrumentIdRepository.findById(id).orElseThrow(() -> new MarketInstrumentIdNotFoundException(id));

        if (marketInstrumentId.getDeleted()) {
            return;
        }

        Instrument instrument = marketInstrumentId.getIdInstrument();

        instrumentService.changeStatus(instrument.getId(),
                instrument.getStatus() == InstrumentStatus.MULTILISTED
                        ? InstrumentStatus.SINGLELISTED
                        : InstrumentStatus.DELISTED);

        marketInstrumentId.setDeleted(true);
        marketInstrumentIdRepository.save(marketInstrumentId);
    }
}

package ru.itmo.lab1.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.lab1.controller.exception.InstrumentNotFoundException;
import ru.itmo.lab1.model.Instrument;
import ru.itmo.lab1.model.dto.InstrumentDto;
import ru.itmo.lab1.model.enums.InstrumentStatus;
import ru.itmo.lab1.repository.InstrumentRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class InstrumentService {
    private InstrumentRepository instrumentRepository;
    private ModelMapper modelMapper;

    public Page<InstrumentDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return instrumentRepository.findAll(pageable).map(market -> modelMapper.map(market, InstrumentDto.class));
    }

    public InstrumentDto findById(UUID id) {
        return instrumentRepository.findById(id).map(instrument -> modelMapper.map(instrument, InstrumentDto.class)).orElseThrow(
                () -> new InstrumentNotFoundException(id)
        );
    }

    public InstrumentDto create(InstrumentDto dto) {
        Instrument instrument = modelMapper.map(dto, Instrument.class);
        instrument.setId(UUID.randomUUID());
        return modelMapper.map(instrumentRepository.save(instrument), InstrumentDto.class);
    }

    public void changeStatus(UUID id, InstrumentStatus status) {
        Instrument instrument = instrumentRepository.findById(id).orElseThrow(
                () -> new InstrumentNotFoundException(id));
        instrument.setStatus(status);
        instrumentRepository.save(instrument);
    }
}

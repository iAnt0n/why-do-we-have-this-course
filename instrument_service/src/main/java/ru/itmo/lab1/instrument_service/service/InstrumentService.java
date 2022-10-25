package ru.itmo.lab1.instrument_service.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.lab1.instrument_service.controller.exception.InstrumentNotFoundException;
import ru.itmo.lab1.instrument_service.model.Instrument;
import ru.itmo.lab1.instrument_service.model.dto.InstrumentDto;
import ru.itmo.lab1.instrument_service.model.dto.InstrumentPatchDto;
import ru.itmo.lab1.instrument_service.model.enums.InstrumentStatus;
import ru.itmo.lab1.instrument_service.repository.InstrumentRepository;

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

    public InstrumentDto patch(UUID id, InstrumentPatchDto instrumentPatchDto) {
        Instrument instrument = instrumentRepository.findById(id).orElseThrow(
                () -> new InstrumentNotFoundException(id));
        instrument.setStatus(instrumentPatchDto.getStatus());
        return modelMapper.map(instrumentRepository.save(instrument), InstrumentDto.class);
    }
}

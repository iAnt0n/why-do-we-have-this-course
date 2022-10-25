package ru.itmo.lab2.market_instrument_id_service.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.itmo.lab2.market_instrument_id_service.client.InstrumentServiceClient;
import ru.itmo.lab2.market_instrument_id_service.model.MarketInstrumentId;
import ru.itmo.lab2.market_instrument_id_service.model.dto.InstrumentDto;
import ru.itmo.lab2.market_instrument_id_service.model.dto.InstrumentPatchDto;
import ru.itmo.lab2.market_instrument_id_service.model.dto.MarketInstrumentIdDto;
import ru.itmo.lab2.market_instrument_id_service.model.enums.InstrumentStatus;
import ru.itmo.lab2.market_instrument_id_service.repository.MarketInstrumentIdRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MarketInstrumentIdService {
    private MarketInstrumentIdRepository marketInstrumentIdRepository;
    private ModelMapper modelMapper;
    private InstrumentServiceClient instrumentServiceClient;

    @HystrixCommand(fallbackMethod = "findAllDefault")
    public Mono<Page<MarketInstrumentIdDto>> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return marketInstrumentIdRepository.findAllBy(pageable)
                .map(marketInstrumentId -> modelMapper.map(marketInstrumentId, MarketInstrumentIdDto.class))
                .collectList()
                .zipWith(this.marketInstrumentIdRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    public Mono<Page<MarketInstrumentIdDto>> findAllDefault(int page, int size) {
        return Mono.just(new PageImpl<>(new ArrayList<>(),PageRequest.of(page, size),0));
    }

    @HystrixCommand(fallbackMethod = "findByIdDefault")
    public Mono<MarketInstrumentIdDto> findById(UUID id) {
        return marketInstrumentIdRepository.findById(id).map(miid -> modelMapper.map(miid, MarketInstrumentIdDto.class));
    }

    public Mono<MarketInstrumentIdDto> findByIdDefault(UUID id) {
        return Mono.just(new MarketInstrumentIdDto());
    }

    @HystrixCommand(fallbackMethod = "createDefault")
    @Transactional
    public Mono<MarketInstrumentIdDto> create(MarketInstrumentIdDto dto) {
        MarketInstrumentId marketInstrumentId = modelMapper.map(dto, MarketInstrumentId.class);
       UUID instrumentId = marketInstrumentId.getIdInstrument();
        return instrumentServiceClient.findById(instrumentId)
                .flatMap(instrumentDto -> {
                    InstrumentStatus status = instrumentDto.getStatus() == InstrumentStatus.DELISTED
                            ? InstrumentStatus.SINGLELISTED
                            : InstrumentStatus.MULTILISTED;
                    return instrumentServiceClient.patchInstrument(instrumentId, new InstrumentPatchDto(status));
                })
                .flatMap(instrumentDto -> saveMarketInstId(marketInstrumentId));
    }

    public Mono<MarketInstrumentIdDto> createDefault(MarketInstrumentIdDto dto) {
        return Mono.just(dto);
    }

    private Mono<MarketInstrumentIdDto> saveMarketInstId(MarketInstrumentId marketInstrumentId) {
        return marketInstrumentIdRepository.save(marketInstrumentId).flatMap(marketInstrument ->
                Mono.just(modelMapper.map(marketInstrument, MarketInstrumentIdDto.class)));
    }

    @HystrixCommand(fallbackMethod = "deleteDefault")
    @Transactional
    public Mono<Void> delete(UUID id) {
        return marketInstrumentIdRepository.findById(id)
                .flatMap(marketInstrumentId -> instrumentServiceClient.findById(marketInstrumentId.getIdInstrument())
                        .map(this::patchInstrument).thenReturn(marketInstrumentId))
                .flatMap(marketInstrumentId -> {
                    marketInstrumentId.setDeleted(true);
                    return marketInstrumentIdRepository.save(marketInstrumentId);
                })
                .then();
    }

    public Mono<Void> deleteDefault(UUID id) {
        return Mono.empty();
    }

    private Mono<InstrumentDto> patchInstrument(InstrumentDto instrumentDto) {
        InstrumentStatus status = instrumentDto.getStatus() == InstrumentStatus.DELISTED
                ? InstrumentStatus.SINGLELISTED
                : InstrumentStatus.MULTILISTED;
        return instrumentServiceClient.patchInstrument(instrumentDto.getId(), new InstrumentPatchDto(status));
    }
}

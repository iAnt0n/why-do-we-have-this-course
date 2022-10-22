package ru.itmo.lab1.instrument_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lab1.instrument_service.model.dto.InstrumentDto;
import ru.itmo.lab1.instrument_service.service.InstrumentService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/instrument")
@AllArgsConstructor
public class InstrumentController {
    private final int defaultPageSize = 50;
    private InstrumentService instrumentService;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) {
        boolean isInfiniteScroll = size == null;
        Page<InstrumentDto> instrumentPage = instrumentService.findAll(page, isInfiniteScroll ? defaultPageSize : size);
        if (instrumentPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if (!isInfiniteScroll) {
            return ResponseEntity.ok()
                    .header("x-total-count", String.valueOf(instrumentPage.getTotalElements()))
                    .body(instrumentPage.getContent());
        }
        return ResponseEntity.ok().body(Map.of(
                "items", instrumentPage.getContent(),
                "hasMore", !instrumentPage.isLast()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstrumentDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(instrumentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<InstrumentDto> createInstrument(@RequestBody InstrumentDto instrument) {
        return new ResponseEntity<>(instrumentService.create(instrument), HttpStatus.CREATED);
    }
}

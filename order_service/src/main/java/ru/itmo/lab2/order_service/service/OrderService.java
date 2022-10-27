package ru.itmo.lab2.order_service.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.lab2.order_service.client.TradeClient;
import ru.itmo.lab2.order_service.controller.exception.OrderNotFoundException;
import ru.itmo.lab2.order_service.controller.exception.OrderStatusConflictException;
import ru.itmo.lab2.order_service.controller.exception.TradeServiceNotAvailableException;
import ru.itmo.lab2.order_service.model.Order;
import ru.itmo.lab2.order_service.model.dto.OrderDto;
import ru.itmo.lab2.order_service.model.dto.TradeDto;
import ru.itmo.lab2.order_service.model.enums.OrderStatus;
import ru.itmo.lab2.order_service.repository.OrderRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {
    private OrderRepository orderRepository;
    private TradeClient tradeClient;
    private ModelMapper modelMapper;

    public Page<OrderDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable).map(order -> modelMapper.map(order, OrderDto.class));
    }

    public OrderDto findById(UUID id) {
        return orderRepository.findById(id).map(order -> modelMapper.map(order, OrderDto.class)).orElseThrow(
                () -> new OrderNotFoundException(id)
        );
    }

    public Page<OrderDto> findAllByIdUser(UUID id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAllByIdUser(id, pageable).map(order -> modelMapper.map(order, OrderDto.class));
    }

    public OrderDto create(OrderDto dto) {
        Order order = modelMapper.map(dto, Order.class);
        order.setId(UUID.randomUUID());
        if (order.getCreatedDatetime() == null) {
            order.setCreatedDatetime(Instant.now());
        }
        return switch (dto.getStatus()) {
            case CANCELLED, REJECTED, FULFILLED -> throw new OrderStatusConflictException(order.getId(), Optional.empty(), order.getStatus());
            case ACTIVE -> modelMapper.map(orderRepository.save(order), OrderDto.class);
        };
    }

    public OrderDto cancel(Order order) {
        switch (order.getStatus()) {
            case CANCELLED:
                break;
            case REJECTED:
            case FULFILLED:
                throw new OrderStatusConflictException(order.getId(), Optional.of(order.getStatus()), OrderStatus.CANCELLED);
            case ACTIVE: {
                order.setStatus(OrderStatus.CANCELLED);
                order = orderRepository.save(order);
            }
        }
        return modelMapper.map(order, OrderDto.class);
    }

    public OrderDto reject(Order order) {
        switch (order.getStatus()) {
            case REJECTED:
                break;
            case CANCELLED:
            case FULFILLED:
                throw new OrderStatusConflictException(order.getId(), Optional.of(order.getStatus()), OrderStatus.REJECTED);
            case ACTIVE: {
                order.setStatus(OrderStatus.REJECTED);
                order = orderRepository.save(order);
            }
        }
        return modelMapper.map(order, OrderDto.class);
    }

    @Transactional
    public OrderDto fulfill(Order order) {
        switch (order.getStatus()) {
            case FULFILLED:
                break;
            case REJECTED:
            case CANCELLED:
                throw new OrderStatusConflictException(order.getId(), Optional.of(order.getStatus()), OrderStatus.FULFILLED);
            case ACTIVE: {
                order.setStatus(OrderStatus.FULFILLED);
                order = orderRepository.save(order);

                TradeDto tradeDto = new TradeDto();
                tradeDto.setIdMiid(order.getIdMiid());
                tradeDto.setPrice(order.getPrice());
                tradeDto.setVolume(order.getVolume());
                tradeDto.setIdOrder(order.getId());
                tradeDto.setIdUser(order.getIdUser());

                if (tradeClient.createTrade(tradeDto) == null) {
                    throw new TradeServiceNotAvailableException();
                }
            }
        }
        return modelMapper.map(order, OrderDto.class);
    }

    public OrderDto updateStatus(UUID id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException(id));
        return switch (status) {
            case ACTIVE -> throw new OrderStatusConflictException(id, Optional.of(OrderStatus.ACTIVE), order.getStatus());
            case CANCELLED -> cancel(order);
            case REJECTED -> reject(order);
            case FULFILLED -> fulfill(order);
        };
    }
}

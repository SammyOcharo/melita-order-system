package com.melita.order_api_service.serviceImpl;

import com.melita.order_api_service.Exceptions.CustomExceptions.DuplicateRequestException;
import com.melita.order_api_service.dto.CreateOrderRequest;
import com.melita.order_api_service.entity.IdempotencyKey;
import com.melita.order_api_service.entity.Order;
import com.melita.order_api_service.kafkaConfig.OrderEventPublisher;
import com.melita.order_api_service.mapper.OrderMapper;
import com.melita.order_api_service.repository.IdempotencyRepository;
import com.melita.order_api_service.repository.OrderRepository;
import com.melita.order_api_service.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final OrderMapper orderMapper;
    private final IdempotencyRepository idempotencyRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEventPublisher eventPublisher, OrderMapper orderMapper, IdempotencyRepository idempotencyRepository) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.orderMapper = orderMapper;
        this.idempotencyRepository = idempotencyRepository;
    }

    @Override
    @Transactional
    public void createOrder(CreateOrderRequest request, String orderIdempotencyKey) {

        Optional<IdempotencyKey> existing = idempotencyRepository.findByIdempotencyKey(orderIdempotencyKey);
        if (existing.isPresent()) {
            throw new DuplicateRequestException("Order with the same Idempotency-Key already exists.");
        }

        Order order = orderMapper.toEntity(request);

        try {
            Order savedOrder = orderRepository.save(order);

            IdempotencyKey record = new IdempotencyKey(
                    orderIdempotencyKey, // e.g., from the header
                    LocalDateTime.now(),
                    order
            );
            idempotencyRepository.save(record);

            eventPublisher.publishOrderCreated(savedOrder);
            log.info("Event published to kafka...");
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Failed to create order. Reason: " + ex.getMessage());
        }
    }
}

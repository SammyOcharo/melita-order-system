package com.melita.order_api_service.serviceImpl;

import com.melita.order_api_service.Exceptions.CustomExceptions.DuplicateRequestException;
import com.melita.order_api_service.dao.OrderItemResponse;
import com.melita.order_api_service.dao.OrderResponse;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final OrderMapper orderMapper;
    private final IdempotencyRepository idempotencyRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderEventPublisher eventPublisher,
                            OrderMapper orderMapper,
                            IdempotencyRepository idempotencyRepository) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.orderMapper = orderMapper;
        this.idempotencyRepository = idempotencyRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String orderIdempotencyKey) {

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

            List<OrderItemResponse> products = savedOrder.getOrderItems().stream()
                    .map(item -> new OrderItemResponse(item.getProductType(), item.getPackageName()))
                    .toList();

            eventPublisher.publishOrderCreated(savedOrder);
            log.info("Event published to kafka...");
            return new OrderResponse(
                    savedOrder.getId().toString(),
                    savedOrder.getOrderNo(),
                    savedOrder.getCustomerName(),
                    savedOrder.getEmail(),
                    savedOrder.getPhone(),
                    savedOrder.getInstallationAddress(),
                    savedOrder.getPreferredDate(),
                    savedOrder.getPreferredTimeSlot(),
                    products,
                    savedOrder.getStatus().name()
            );

        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Failed to create order. Reason: " + ex.getMessage());
        }
    }

    @Override
    public OrderResponse getOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemResponse> products = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(item.getProductType(), item.getPackageName()))
                .toList();

        return new OrderResponse(
                order.getId().toString(),
                order.getOrderNo(),
                order.getCustomerName(),
                order.getEmail(),
                order.getPhone(),
                order.getInstallationAddress(),
                order.getPreferredDate(),
                order.getPreferredTimeSlot(),
                products,
                order.getStatus().name()
        );
    }
}

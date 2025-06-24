package com.melita.order_api_service.serviceImpl;

import com.melita.order_api_service.Exceptions.CustomExceptions.DuplicateRequestException;
import com.melita.order_api_service.TestData;
import com.melita.order_api_service.dto.CreateOrderRequest;
import com.melita.order_api_service.entity.IdempotencyKey;
import com.melita.order_api_service.entity.Order;
import com.melita.order_api_service.kafkaConfig.OrderEventPublisher;
import com.melita.order_api_service.mapper.OrderMapper;
import com.melita.order_api_service.repository.IdempotencyRepository;
import com.melita.order_api_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private IdempotencyRepository idempotencyRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderSuccessfully() {
        CreateOrderRequest request = TestData.createValidOrderRequest();
        Order mappedOrder = new Order();
        Order savedOrder = new Order();
        savedOrder.setId(UUID.randomUUID());

        when(idempotencyRepository.findByIdempotencyKey("KEY123")).thenReturn(Optional.empty());
        when(orderMapper.toEntity(request)).thenReturn(mappedOrder);
        when(orderRepository.save(mappedOrder)).thenReturn(savedOrder);

        orderService.createOrder(request, "KEY123");

        verify(orderRepository).save(mappedOrder);
        verify(idempotencyRepository).save(any(IdempotencyKey.class));
        verify(eventPublisher).publishOrderCreated(savedOrder);
    }

    @Test
    void shouldThrowDuplicateRequestException() {
        String key = "DUPLICATE";
        when(idempotencyRepository.findByIdempotencyKey(key))
                .thenReturn(Optional.of(mock(IdempotencyKey.class)));

        DuplicateRequestException ex = assertThrows(
                DuplicateRequestException.class,
                () -> orderService.createOrder(new CreateOrderRequest(), key)
        );

        assertEquals("Order with the same Idempotency-Key already exists.", ex.getMessage());
    }

    @Test
    void shouldThrowIllegalStateOnDataIntegrityViolation() {
        CreateOrderRequest request = TestData.createValidOrderRequest();
        Order mappedOrder = new Order();

        when(idempotencyRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(orderMapper.toEntity(request)).thenReturn(mappedOrder);
        when(orderRepository.save(mappedOrder))
                .thenThrow(new DataIntegrityViolationException("DB violation"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                orderService.createOrder(request, "KEY456"));

        assertTrue(ex.getMessage().contains("Failed to create order"));
    }
}

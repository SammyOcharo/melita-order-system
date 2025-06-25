package com.melita.order_api_service.service;

import com.melita.order_api_service.dao.OrderResponse;
import com.melita.order_api_service.dto.CreateOrderRequest;
import jakarta.validation.Valid;

public interface OrderService {
    OrderResponse createOrder(@Valid CreateOrderRequest request, String orderIdempotencyKey);

    OrderResponse getOrder(String orderNo);

}

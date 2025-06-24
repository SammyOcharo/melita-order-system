package com.melita.order_api_service.service;

import com.melita.order_api_service.dto.CreateOrderRequest;

public interface OrderService {
    void createOrder(CreateOrderRequest request, String orderIdempotencyKey);
}

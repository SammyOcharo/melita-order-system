package com.melita.order_api_service.kafkaConfig;

import com.melita.order_api_service.entity.Order;

public interface OrderEventPublisher {
    void publishOrderCreated(Order order);
}
package com.melita.order_fulfilment_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderFulfilmentConsumerService {

    private static final Logger log = LoggerFactory.getLogger(OrderFulfilmentConsumerService.class);

    @KafkaListener(topics = "melita.orders.created", groupId = "order-fulfilment-group")
    public void listen(String message) {
        log.info("Received order message: {}", message);
    }
}
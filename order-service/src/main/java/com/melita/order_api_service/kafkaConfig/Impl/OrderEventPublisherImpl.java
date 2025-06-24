package com.melita.order_api_service.kafkaConfig.Impl;

import com.melita.order_api_service.entity.Order;
import com.melita.order_api_service.kafkaConfig.KafkaTopics;
import com.melita.order_api_service.kafkaConfig.OrderEventPublisher;
import com.melita.order_api_service.kafkaConfig.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventPublisherImpl(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getId());
        event.setEmail(order.getEmail());
        event.setPreferredDate(order.getPreferredDate());
        event.setTimeSlot(order.getPreferredTimeSlot());

        List<OrderCreatedEvent.OrderItemEvent> items = order.getOrderItems().stream()
                .map(item -> {
                    OrderCreatedEvent.OrderItemEvent i = new OrderCreatedEvent.OrderItemEvent();
                    i.setProductType(item.getProductType());
                    i.setPackageName(item.getPackageName());
                    return i;
                })
                .collect(Collectors.toList());

        event.setItems(items);

        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event);
    }
}
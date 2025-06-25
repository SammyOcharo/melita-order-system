package com.melita.order_api_service.kafkaConfig.event;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class OrderCreatedEvent {
    private UUID orderId;
    private String email;
    private LocalDate preferredDate;
    private String timeSlot;
    private String status;
    private List<OrderItemEvent> items;

    private String eventVersion = "v1";

    @Data
    public static class OrderItemEvent {
        private String productType;
        private String packageName;
    }
}

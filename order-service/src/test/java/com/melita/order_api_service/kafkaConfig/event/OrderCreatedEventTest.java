package com.melita.order_api_service.kafkaConfig.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderCreatedEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void shouldSerializeAndDeserializeOrderCreatedEvent() throws Exception {
        OrderCreatedEvent.OrderItemEvent item = new OrderCreatedEvent.OrderItemEvent();
        item.setProductType("Internet");
        item.setPackageName("250Mbps");

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(UUID.randomUUID());
        event.setEmail("john@example.com");
        event.setPreferredDate(LocalDate.now());
        event.setTimeSlot("10:00 AM - 12:00 PM");
        event.setItems(List.of(item));

        // Serialize
        String json = objectMapper.writeValueAsString(event);

        // Deserialize
        OrderCreatedEvent result = objectMapper.readValue(json, OrderCreatedEvent.class);

        assertEquals(event.getEmail(), result.getEmail());
        assertEquals(event.getItems().size(), result.getItems().size());
        assertEquals("250Mbps", result.getItems().get(0).getPackageName());
    }
}
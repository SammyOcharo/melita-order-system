package com.melita.order_api_service.mapper.mapperImp;

import com.melita.order_api_service.dto.CreateOrderRequest;
import com.melita.order_api_service.dto.OrderItemRequest;
import com.melita.order_api_service.entity.Order;
import com.melita.order_api_service.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperImplTest {

    private final OrderMapperImpl orderMapper = new OrderMapperImpl();

    @Test
    void shouldMapCreateOrderRequestToOrderEntity() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("John Doe");
        request.setEmail("john@example.com");
        request.setPhone("+254712345678");
        request.setInstallationAddress("123 Juja Street, Nairobi");
        request.setPreferredDate(LocalDate.of(2025, 6, 30));
        request.setPreferredTimeSlot("2:00 PM - 4:00 PM");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductType("Internet");
        itemRequest.setPackageName("250Mbps");

        request.setProducts(List.of(itemRequest));

        // When
        Order order = orderMapper.toEntity(request);

        // Then
        assertEquals("John Doe", order.getCustomerName());
        assertEquals("john@example.com", order.getEmail());
        assertEquals("+254712345678", order.getPhone());
        assertEquals("123 Juja Street, Nairobi", order.getInstallationAddress());
        assertEquals(LocalDate.of(2025, 6, 30), order.getPreferredDate());
        assertEquals("2:00 PM - 4:00 PM", order.getPreferredTimeSlot());
        assertEquals(OrderStatus.PENDING, order.getStatus());

        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getOrderNo());
        assertTrue(order.getOrderNo().startsWith("ORD-"));

        assertNotNull(order.getOrderItems());
        assertEquals(1, order.getOrderItems().size());
        assertEquals("Internet", order.getOrderItems().get(0).getProductType());
        assertEquals("250Mbps", order.getOrderItems().get(0).getPackageName());
        assertEquals(order, order.getOrderItems().get(0).getOrder());
    }
}

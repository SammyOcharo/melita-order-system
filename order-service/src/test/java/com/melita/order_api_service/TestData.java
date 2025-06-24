package com.melita.order_api_service;

import com.melita.order_api_service.dto.CreateOrderRequest;
import com.melita.order_api_service.dto.OrderItemRequest;

import java.time.LocalDate;
import java.util.List;

public class TestData {

    public static CreateOrderRequest createValidOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("0700123456");
        request.setInstallationAddress("Juja, Thika Road");
        request.setPreferredDate(LocalDate.of(2025, 7, 1));
        request.setPreferredTimeSlot("10:00-12:00");

        OrderItemRequest internet = new OrderItemRequest();
        internet.setProductType("Internet");
        internet.setPackageName("1Gbps");

        OrderItemRequest tv = new OrderItemRequest();
        tv.setProductType("TV");
        tv.setPackageName("90 Channels");

        request.setProducts(List.of(internet, tv));

        return request;
    }
}

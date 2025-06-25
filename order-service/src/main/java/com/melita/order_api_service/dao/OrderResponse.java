package com.melita.order_api_service.dao;

import java.time.LocalDate;
import java.util.List;


public record OrderResponse(
        String id,
        String orderNo,
        String customerName,
        String email,
        String phone,
        String installationAddress,
        LocalDate preferredDate,
        String preferredTimeSlot,
        List<OrderItemResponse> products,
        String status
) {}
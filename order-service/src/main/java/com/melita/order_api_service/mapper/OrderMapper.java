package com.melita.order_api_service.mapper;

import com.melita.order_api_service.dto.CreateOrderRequest;
import com.melita.order_api_service.entity.Order;

public interface OrderMapper {
    Order toEntity(CreateOrderRequest request);
}

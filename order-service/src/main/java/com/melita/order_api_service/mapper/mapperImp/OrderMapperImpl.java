package com.melita.order_api_service.mapper.mapperImp;

import com.melita.order_api_service.dto.CreateOrderRequest;
import com.melita.order_api_service.entity.Order;
import com.melita.order_api_service.entity.OrderItem;
import com.melita.order_api_service.entity.OrderStatus;
import com.melita.order_api_service.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toEntity(CreateOrderRequest request) {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setCustomerName(request.getCustomerName());
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setOrderNo(generateOrderNo());
        order.setInstallationAddress(request.getInstallationAddress());
        order.setPreferredDate(request.getPreferredDate());
        order.setPreferredTimeSlot(request.getPreferredTimeSlot());
        order.setStatus(OrderStatus.PENDING);

        order.setOrderItems(request.getProducts()
                .stream()
                .map(itemReq -> {
                    OrderItem item = new OrderItem();
                    item.setProductType(itemReq.getProductType());
                    item.setPackageName(itemReq.getPackageName());
                    item.setOrder(order);
                    return item;
                })
                .collect(Collectors.toList()));

        return order;
    }

    public static String generateOrderNo() {
        // Format: ORD-20250625-4F6A7C
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + date + "-" + randomPart;
    }
}

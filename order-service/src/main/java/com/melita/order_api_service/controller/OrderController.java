package com.melita.order_api_service.controller;

import com.melita.order_api_service.dao.OrderResponse;
import com.melita.order_api_service.dto.CreateOrderRequest;
import com.melita.order_api_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                              @RequestHeader("orderIdempotency-Key") String orderIdempotencyKey) {
        ;
        return ResponseEntity.ok(orderService.createOrder(request, orderIdempotencyKey));
    }

    @GetMapping("/{orderNo}")
    public  ResponseEntity<OrderResponse> getOrder(@PathVariable String orderNo){
        return ResponseEntity.ok(orderService.getOrder(orderNo));
    }
}

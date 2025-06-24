package com.melita.order_api_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class OrderItemRequest {

    @NotBlank
    private String productType;

    @NotBlank
    private String packageName;
}
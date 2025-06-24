package com.melita.order_api_service.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int statusCode;
    private final String statusMessage;
    private final String message;

    public ErrorResponse(int statusCode, String statusMessage, String message) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.message = message;
    }
}
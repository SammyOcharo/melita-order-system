package com.melita.order_api_service.Exceptions.CustomExceptions;

public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String message) {
        super(message);
    }
}
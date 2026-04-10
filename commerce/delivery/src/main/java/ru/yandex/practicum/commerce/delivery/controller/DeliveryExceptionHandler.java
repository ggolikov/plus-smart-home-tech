package ru.yandex.practicum.commerce.delivery.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryErrorResponse;
import ru.yandex.practicum.commerce.delivery.service.NoDeliveryFoundException;

@RestControllerAdvice
public class DeliveryExceptionHandler {

    @ExceptionHandler(NoDeliveryFoundException.class)
    public ResponseEntity<DeliveryErrorResponse> notFound(NoDeliveryFoundException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new DeliveryErrorResponse(message, message, "404 NOT_FOUND")
        );
    }
}

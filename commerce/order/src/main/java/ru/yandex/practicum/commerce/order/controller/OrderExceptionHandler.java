package ru.yandex.practicum.commerce.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.dto.order.OrderErrorResponse;
import ru.yandex.practicum.commerce.order.service.NoOrderFoundException;
import ru.yandex.practicum.commerce.order.service.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.order.service.NotAuthorizedUserException;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<OrderErrorResponse> unauthorized(NotAuthorizedUserException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new OrderErrorResponse(message, message, "401 UNAUTHORIZED")
        );
    }

    @ExceptionHandler({NoOrderFoundException.class, IllegalArgumentException.class})
    public ResponseEntity<OrderErrorResponse> badRequest(RuntimeException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new OrderErrorResponse(message, message, "400 BAD_REQUEST")
        );
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<OrderErrorResponse> noProductInWarehouse(NoSpecifiedProductInWarehouseException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new OrderErrorResponse(message, message, "400 BAD_REQUEST")
        );
    }
}

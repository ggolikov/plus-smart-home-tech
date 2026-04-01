package ru.yandex.practicum.commerce.shoppingstore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductNotFoundException;

@RestControllerAdvice
public class ShoppingStoreExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProductNotFoundResponse> handleNotFound(ProductNotFoundException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ProductNotFoundResponse(message, message, "404 NOT_FOUND")
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProductNotFoundResponse> handleBadRequest(IllegalArgumentException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ProductNotFoundResponse(message, message, "400 BAD_REQUEST")
        );
    }

    public record ProductNotFoundResponse(String userMessage, String message, String httpStatus) {
    }
}

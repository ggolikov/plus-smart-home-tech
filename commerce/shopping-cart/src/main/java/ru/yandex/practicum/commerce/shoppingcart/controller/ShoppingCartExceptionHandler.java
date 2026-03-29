package ru.yandex.practicum.commerce.shoppingcart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.dto.shopping.ShoppingCartErrorResponse;
import ru.yandex.practicum.commerce.shoppingcart.service.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.shoppingcart.service.NotAuthorizedUserException;

@RestControllerAdvice
public class ShoppingCartExceptionHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ShoppingCartErrorResponse> handleUnauthorized(NotAuthorizedUserException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ShoppingCartErrorResponse(message, message, "401 UNAUTHORIZED")
        );
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<ShoppingCartErrorResponse> handleBadRequest(NoProductsInShoppingCartException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ShoppingCartErrorResponse(message, message, "400 BAD_REQUEST")
        );
    }
}

package ru.yandex.practicum.commerce.warehouse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.warehouse.service.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.service.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.service.SpecifiedProductAlreadyInWarehouseException;

@RestControllerAdvice
public class WarehouseExceptionHandler {

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<WarehouseErrorResponse> handleDuplicate(SpecifiedProductAlreadyInWarehouseException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new WarehouseErrorResponse(message, message, "400 BAD_REQUEST")
        );
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouseException.class)
    public ResponseEntity<WarehouseErrorResponse> handleLowQuantity(
            ProductInShoppingCartLowQuantityInWarehouseException ex
    ) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new WarehouseErrorResponse(message, message, "400 BAD_REQUEST")
        );
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<WarehouseErrorResponse> handleMissingProduct(NoSpecifiedProductInWarehouseException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new WarehouseErrorResponse(message, message, "400 BAD_REQUEST")
        );
    }
}

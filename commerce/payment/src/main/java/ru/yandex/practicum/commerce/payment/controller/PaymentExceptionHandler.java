package ru.yandex.practicum.commerce.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.dto.payment.PaymentErrorResponse;
import ru.yandex.practicum.commerce.payment.service.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.payment.service.PaymentNotFoundException;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    public ResponseEntity<PaymentErrorResponse> notEnoughInfo(NotEnoughInfoInOrderToCalculateException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new PaymentErrorResponse(message, message, "400 BAD_REQUEST")
        );
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<PaymentErrorResponse> notFound(PaymentNotFoundException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new PaymentErrorResponse(message, message, "404 NOT_FOUND")
        );
    }
}

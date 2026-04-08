package ru.yandex.practicum.commerce.payment.service;

public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {

    public NotEnoughInfoInOrderToCalculateException(String message) {
        super(message);
    }
}

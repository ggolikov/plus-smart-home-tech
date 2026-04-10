package ru.yandex.practicum.commerce.delivery.service;

public class NoDeliveryFoundException extends RuntimeException {

    public NoDeliveryFoundException(String message) {
        super(message);
    }
}

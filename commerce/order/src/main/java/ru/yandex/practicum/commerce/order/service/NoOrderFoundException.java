package ru.yandex.practicum.commerce.order.service;

public class NoOrderFoundException extends RuntimeException {

    public NoOrderFoundException(String message) {
        super(message);
    }
}

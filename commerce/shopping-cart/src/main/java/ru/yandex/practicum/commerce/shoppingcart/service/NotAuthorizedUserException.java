package ru.yandex.practicum.commerce.shoppingcart.service;

public class NotAuthorizedUserException extends RuntimeException {

    public NotAuthorizedUserException(String message) {
        super(message);
    }
}

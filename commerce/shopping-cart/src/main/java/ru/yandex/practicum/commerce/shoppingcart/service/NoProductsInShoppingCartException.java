package ru.yandex.practicum.commerce.shoppingcart.service;

public class NoProductsInShoppingCartException extends RuntimeException {

    public NoProductsInShoppingCartException(String message) {
        super(message);
    }
}

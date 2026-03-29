package ru.yandex.practicum.commerce.warehouse.service;

public class ProductInShoppingCartLowQuantityInWarehouseException extends RuntimeException {

    public ProductInShoppingCartLowQuantityInWarehouseException(String message) {
        super(message);
    }
}

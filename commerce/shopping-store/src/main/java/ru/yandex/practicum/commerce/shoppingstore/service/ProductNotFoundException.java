package ru.yandex.practicum.commerce.shoppingstore.service;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    private final UUID productId;

    public ProductNotFoundException(UUID productId) {
        super("Product not found: " + productId);
        this.productId = productId;
    }

    public UUID getProductId() {
        return productId;
    }
}

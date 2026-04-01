package ru.yandex.practicum.commerce.shoppingcart.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.cart.ShoppingCartOperations;
import ru.yandex.practicum.commerce.dto.shopping.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.shopping.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API корзины по спецификации {@code commerce/openapi/shopping-cart.json}.
 */
@RestController
public class ShoppingCartController {

    private final ShoppingCartOperations shoppingCart;

    public ShoppingCartController(ShoppingCartOperations shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    @GetMapping(value = "/api/v1/shopping-cart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ShoppingCartDto getShoppingCart(@RequestParam("username") String username) {
        return shoppingCart.getShoppingCart(username);
    }

    @PutMapping(
            value = "/api/v1/shopping-cart",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ShoppingCartDto addProductToShoppingCart(
            @RequestParam("username") String username,
            @RequestBody Map<UUID, Long> productIdToQuantity
    ) {
        return shoppingCart.addProductToShoppingCart(username, productIdToQuantity);
    }

    @DeleteMapping("/api/v1/shopping-cart")
    public void deactivateCurrentShoppingCart(@RequestParam("username") String username) {
        shoppingCart.deactivateCurrentShoppingCart(username);
    }

    @PostMapping(
            value = "/api/v1/shopping-cart/remove",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ShoppingCartDto removeFromShoppingCart(
            @RequestParam("username") String username,
            @RequestBody List<UUID> productIds
    ) {
        return shoppingCart.removeFromShoppingCart(username, productIds);
    }

    @PostMapping(
            value = "/api/v1/shopping-cart/change-quantity",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ShoppingCartDto changeProductQuantity(
            @RequestParam("username") String username,
            @Valid @RequestBody ChangeProductQuantityRequest request
    ) {
        return shoppingCart.changeProductQuantity(username, request);
    }
}

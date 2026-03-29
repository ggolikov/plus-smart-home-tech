package ru.yandex.practicum.commerce.shoppingstore.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperations;
import ru.yandex.practicum.commerce.dto.shopping.store.PageProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@RestController
public class ShoppingStoreController {

    private final ShoppingStoreOperations shoppingStore;

    public ShoppingStoreController(ShoppingStoreOperations shoppingStore) {
        this.shoppingStore = shoppingStore;
    }

    @GetMapping(value = "/api/v1/shopping-store", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageProductDto getProducts(
            @RequestParam("category") ProductCategory category,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") List<String> sort
    ) {
        return shoppingStore.getProducts(category, page, size, sort);
    }

    @PutMapping(
            value = "/api/v1/shopping-store",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ProductDto createNewProduct(@Valid @RequestBody ProductDto product) {
        return shoppingStore.createNewProduct(product);
    }

    @PostMapping(
            value = "/api/v1/shopping-store",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ProductDto updateProduct(@Valid @RequestBody ProductDto product) {
        return shoppingStore.updateProduct(product);
    }

    @PostMapping(
            value = "/api/v1/shopping-store/removeProductFromStore",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Boolean removeProductFromStore(@RequestBody UUID productId) {
        return shoppingStore.removeProductFromStore(productId);
    }

    @PostMapping(
            value = "/api/v1/shopping-store/quantityState",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Boolean setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request) {
        return shoppingStore.setProductQuantityState(request);
    }

    @GetMapping(
            value = "/api/v1/shopping-store/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ProductDto getProduct(@PathVariable("productId") UUID productId) {
        return shoppingStore.getProduct(productId);
    }
}

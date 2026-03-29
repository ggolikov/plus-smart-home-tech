package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperations;
import ru.yandex.practicum.commerce.dto.shopping.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;

/**
 * REST API склада по спецификации {@code commerce/openapi/warehouse.json}.
 */
@RestController
public class WarehouseController {

    private final WarehouseOperations warehouseOperations;

    public WarehouseController(WarehouseOperations warehouseOperations) {
        this.warehouseOperations = warehouseOperations;
    }

    @PutMapping(
            value = "/api/v1/warehouse",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest request) {
        warehouseOperations.newProductInWarehouse(request);
    }

    @PostMapping(
            value = "/api/v1/warehouse/check",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(
            @Valid @RequestBody ShoppingCartDto shoppingCart
    ) {
        return warehouseOperations.checkProductQuantityEnoughForShoppingCart(shoppingCart);
    }

    @PostMapping(
            value = "/api/v1/warehouse/add",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request) {
        warehouseOperations.addProductToWarehouse(request);
    }

    @GetMapping(
            value = "/api/v1/warehouse/address",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AddressDto getWarehouseAddress() {
        return warehouseOperations.getWarehouseAddress();
    }
}

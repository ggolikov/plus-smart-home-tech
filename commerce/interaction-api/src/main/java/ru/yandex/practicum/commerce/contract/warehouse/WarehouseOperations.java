package ru.yandex.practicum.commerce.contract.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.commerce.dto.shopping.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;

@FeignClient(
        name = "warehouse",
        url = "${commerce.warehouse.url:http://localhost:38081}"
)
public interface WarehouseOperations {

    @PutMapping(
            value = "/api/v1/warehouse",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping(
            value = "/api/v1/warehouse/check",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto shoppingCart);

    @PostMapping(
            value = "/api/v1/warehouse/add",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping(
            value = "/api/v1/warehouse/address",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    AddressDto getWarehouseAddress();
}

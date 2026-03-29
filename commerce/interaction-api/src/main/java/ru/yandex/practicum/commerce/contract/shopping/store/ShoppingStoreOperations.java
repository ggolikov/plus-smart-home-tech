package ru.yandex.practicum.commerce.contract.shopping.store;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import ru.yandex.practicum.commerce.dto.shopping.store.PageProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "shopping-store",
        url = "${commerce.shopping-store.url:http://localhost:8080}"
)
public interface ShoppingStoreOperations {

    @GetMapping(value = "/api/v1/shopping-store", produces = MediaType.APPLICATION_JSON_VALUE)
    PageProductDto getProducts(
            @RequestParam("category") ProductCategory category,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") List<String> sort
    );

    @PutMapping(
            value = "/api/v1/shopping-store",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ProductDto createNewProduct(@RequestBody ProductDto product);

    @PostMapping(
            value = "/api/v1/shopping-store",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ProductDto updateProduct(@RequestBody ProductDto product);

    @PostMapping(
            value = "/api/v1/shopping-store/removeProductFromStore",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping(
            value = "/api/v1/shopping-store/quantityState",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping(
            value = "/api/v1/shopping-store/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ProductDto getProduct(@PathVariable("productId") UUID productId);
}

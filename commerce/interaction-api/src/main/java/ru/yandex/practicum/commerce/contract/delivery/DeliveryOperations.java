package ru.yandex.practicum.commerce.contract.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(
        name = "delivery",
        url = "${commerce.delivery.url:http://localhost:44989}"
)
public interface DeliveryOperations {

    @PutMapping(
            value = "/api/v1/delivery",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    DeliveryDto planDelivery(@RequestBody DeliveryDto delivery);

    @PostMapping(
            value = "/api/v1/delivery/successful",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void deliverySuccessful(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/delivery/picked",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void deliveryPicked(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/delivery/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void deliveryFailed(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/delivery/cost",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    BigDecimal deliveryCost(@RequestBody OrderDto order);
}

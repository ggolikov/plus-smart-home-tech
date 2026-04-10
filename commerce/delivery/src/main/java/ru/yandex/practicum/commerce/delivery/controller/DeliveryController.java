package ru.yandex.practicum.commerce.delivery.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.delivery.DeliveryOperations;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST API доставки по спецификации {@code commerce/openapi/delivery.json}.
 */
@RestController
public class DeliveryController {

    private final DeliveryOperations deliveryOperations;

    public DeliveryController(DeliveryOperations deliveryOperations) {
        this.deliveryOperations = deliveryOperations;
    }

    @PutMapping(
            value = "/api/v1/delivery",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto delivery) {
        return deliveryOperations.planDelivery(delivery);
    }

    @PostMapping(
            value = "/api/v1/delivery/successful",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void deliverySuccessful(@RequestBody UUID orderId) {
        deliveryOperations.deliverySuccessful(orderId);
    }

    @PostMapping(
            value = "/api/v1/delivery/picked",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void deliveryPicked(@RequestBody UUID orderId) {
        deliveryOperations.deliveryPicked(orderId);
    }

    @PostMapping(
            value = "/api/v1/delivery/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void deliveryFailed(@RequestBody UUID orderId) {
        deliveryOperations.deliveryFailed(orderId);
    }

    @PostMapping(
            value = "/api/v1/delivery/cost",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BigDecimal deliveryCost(@Valid @RequestBody OrderDto order) {
        return deliveryOperations.deliveryCost(order);
    }
}

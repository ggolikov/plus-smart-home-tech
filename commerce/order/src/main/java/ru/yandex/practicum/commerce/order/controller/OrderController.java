package ru.yandex.practicum.commerce.order.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.order.OrderOperations;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

/**
 * REST API заказов по спецификации {@code commerce/openapi/order.json}.
 */
@RestController
public class OrderController {

    private final OrderOperations orderOperations;

    public OrderController(OrderOperations orderOperations) {
        this.orderOperations = orderOperations;
    }

    @GetMapping(value = "/api/v1/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDto> getClientOrders(@RequestParam("username") String username) {
        return orderOperations.getClientOrders(username);
    }

    @PutMapping(
            value = "/api/v1/order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest request) {
        return orderOperations.createNewOrder(request);
    }

    @PostMapping(
            value = "/api/v1/order/return",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto productReturn(@Valid @RequestBody ProductReturnRequest request) {
        return orderOperations.productReturn(request);
    }

    @PostMapping(
            value = "/api/v1/order/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto payment(@RequestBody UUID orderId) {
        return orderOperations.payment(orderId);
    }

    @PostMapping(
            value = "/api/v1/order/payment/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto paymentFailed(@RequestBody UUID orderId) {
        return orderOperations.paymentFailed(orderId);
    }

    @PostMapping(
            value = "/api/v1/order/delivery",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto delivery(@RequestBody UUID orderId) {
        return orderOperations.delivery(orderId);
    }

    @PostMapping(
            value = "/api/v1/order/delivery/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto deliveryFailed(@RequestBody UUID orderId) {
        return orderOperations.deliveryFailed(orderId);
    }

    @PostMapping(
            value = "/api/v1/order/completed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto complete(@RequestBody UUID orderId) {
        return orderOperations.complete(orderId);
    }

    @PostMapping(
            value = "/api/v1/order/calculate/total",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto calculateTotalCost(@RequestBody UUID orderId) {
        return orderOperations.calculateTotalCost(orderId);
    }

    @PostMapping(
            value = "/api/v1/order/calculate/delivery",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        return orderOperations.calculateDeliveryCost(orderId);
    }

    @PostMapping(
            value = "/api/v1/order/assembly",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto assembly(@RequestBody UUID orderId) {
        return orderOperations.assembly(orderId);
    }

    @PostMapping(
            value = "/api/v1/order/assembly/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderDto assemblyFailed(@RequestBody UUID orderId) {
        return orderOperations.assemblyFailed(orderId);
    }
}

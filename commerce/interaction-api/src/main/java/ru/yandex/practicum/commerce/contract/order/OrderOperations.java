package ru.yandex.practicum.commerce.contract.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "order",
        url = "${commerce.order.url:http://localhost:41723}"
)
public interface OrderOperations {

    @GetMapping(value = "/api/v1/order", produces = MediaType.APPLICATION_JSON_VALUE)
    List<OrderDto> getClientOrders(@RequestParam("username") String username);

    @PutMapping(
            value = "/api/v1/order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping(
            value = "/api/v1/order/return",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto productReturn(@RequestBody ProductReturnRequest request);

    @PostMapping(
            value = "/api/v1/order/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto payment(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/order/payment/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto paymentFailed(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/order/delivery",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto delivery(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/order/delivery/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto deliveryFailed(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/order/completed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto complete(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/order/calculate/total",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto calculateTotalCost(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/order/calculate/delivery",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto calculateDeliveryCost(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/order/assembly",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto assembly(@RequestBody UUID orderId);

    @PostMapping(
            value = "/api/v1/order/assembly/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    OrderDto assemblyFailed(@RequestBody UUID orderId);
}

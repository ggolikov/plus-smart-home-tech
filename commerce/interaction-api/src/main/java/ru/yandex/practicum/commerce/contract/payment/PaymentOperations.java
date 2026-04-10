package ru.yandex.practicum.commerce.contract.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(
        name = "payment",
        url = "${commerce.payment.url:http://localhost:51887}"
)
public interface PaymentOperations {

    @PostMapping(
            value = "/api/v1/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    PaymentDto payment(@RequestBody OrderDto order);

    @PostMapping(
            value = "/api/v1/payment/totalCost",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    BigDecimal getTotalCost(@RequestBody OrderDto order);

    @PostMapping(
            value = "/api/v1/payment/productCost",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    BigDecimal productCost(@RequestBody OrderDto order);

    @PostMapping(
            value = "/api/v1/payment/refund",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping(
            value = "/api/v1/payment/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void paymentFailed(@RequestBody UUID paymentId);
}

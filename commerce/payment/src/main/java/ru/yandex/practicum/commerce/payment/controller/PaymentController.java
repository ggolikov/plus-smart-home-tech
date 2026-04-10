package ru.yandex.practicum.commerce.payment.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.payment.PaymentOperations;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Платежный API по спецификации {@code commerce/openapi/payment.json}.
 */
@RestController
public class PaymentController {

    private final PaymentOperations paymentOperations;

    public PaymentController(PaymentOperations paymentOperations) {
        this.paymentOperations = paymentOperations;
    }

    @PostMapping(
            value = "/api/v1/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PaymentDto payment(@Valid @RequestBody OrderDto order) {
        return paymentOperations.payment(order);
    }

    @PostMapping(
            value = "/api/v1/payment/totalCost",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BigDecimal getTotalCost(@Valid @RequestBody OrderDto order) {
        return paymentOperations.getTotalCost(order);
    }

    @PostMapping(
            value = "/api/v1/payment/productCost",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BigDecimal productCost(@Valid @RequestBody OrderDto order) {
        return paymentOperations.productCost(order);
    }

    @PostMapping(
            value = "/api/v1/payment/refund",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void paymentSuccess(@RequestBody UUID paymentId) {
        paymentOperations.paymentSuccess(paymentId);
    }

    @PostMapping(
            value = "/api/v1/payment/failed",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void paymentFailed(@RequestBody UUID paymentId) {
        paymentOperations.paymentFailed(paymentId);
    }
}

package ru.yandex.practicum.commerce.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.payment.PaymentOperations;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;
import ru.yandex.practicum.commerce.payment.repository.entity.PaymentEntity;
import ru.yandex.practicum.commerce.payment.repository.entity.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class PaymentService implements PaymentOperations {

    private static final BigDecimal STUB_UNIT_PRICE = new BigDecimal("100.00");
    private static final BigDecimal FEE_RATE = new BigDecimal("0.05");

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        BigDecimal product = resolveProductCost(order);
        BigDecimal delivery = resolveDeliveryCost(order, product);
        BigDecimal base = product.add(delivery);
        BigDecimal fee = base.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = base.add(fee);

        UUID paymentId = UUID.randomUUID();
        PaymentEntity entity = new PaymentEntity();
        entity.setPaymentId(paymentId);
        entity.setOrderId(order.orderId());
        entity.setTotalPayment(total);
        entity.setDeliveryTotal(delivery);
        entity.setFeeTotal(fee);
        entity.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(entity);

        return new PaymentDto(paymentId, total, delivery, fee);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto order) {
        if (order.totalPrice() != null) {
            return order.totalPrice();
        }
        BigDecimal product = resolveProductCost(order);
        BigDecimal delivery = order.deliveryPrice() != null ? order.deliveryPrice() : BigDecimal.ZERO;
        return product.add(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto order) {
        return resolveProductCost(order);
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        entity.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(entity);
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        entity.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(entity);
    }

    private BigDecimal resolveProductCost(OrderDto order) {
        if (order.productPrice() != null) {
            return order.productPrice();
        }
        BigDecimal stub = stubFromProducts(order);
        if (stub == null) {
            throw new NotEnoughInfoInOrderToCalculateException(
                    "Not enough data in order to calculate product cost"
            );
        }
        return stub;
    }

    private BigDecimal resolveDeliveryCost(OrderDto order, BigDecimal product) {
        if (order.deliveryPrice() != null) {
            return order.deliveryPrice();
        }
        if (order.totalPrice() != null) {
            return order.totalPrice().subtract(product).max(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    private static BigDecimal stubFromProducts(OrderDto order) {
        if (order.products() == null || order.products().isEmpty()) {
            return null;
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (Long qty : order.products().values()) {
            if (qty == null || qty <= 0) {
                continue;
            }
            sum = sum.add(STUB_UNIT_PRICE.multiply(BigDecimal.valueOf(qty)));
        }
        return sum.compareTo(BigDecimal.ZERO) > 0 ? sum : null;
    }
}

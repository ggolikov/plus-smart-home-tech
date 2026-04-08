package ru.yandex.practicum.commerce.delivery.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.delivery.DeliveryOperations;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.delivery.repository.entity.DeliveryEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class DeliveryService implements DeliveryOperations {

    private static final BigDecimal DEFAULT_ROUTE_COST = new BigDecimal("750.00");

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto delivery) {
        DeliveryEntity entity = deliveryRepository.findByOrderId(delivery.orderId())
                .orElseGet(DeliveryEntity::new);

        if (entity.getDeliveryId() == null) {
            entity.setDeliveryId(
                    delivery.deliveryId() != null ? delivery.deliveryId() : UUID.randomUUID()
            );
            entity.setOrderId(delivery.orderId());
        }

        applyAddresses(entity, delivery);
        entity.setState(delivery.deliveryState());
        entity.setEstimatedCost(estimateRouteCost(delivery));

        return toDto(deliveryRepository.save(entity));
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID orderId) {
        DeliveryEntity entity = requireByOrderId(orderId);
        entity.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(entity);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) {
        DeliveryEntity entity = requireByOrderId(orderId);
        entity.setState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(entity);
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        DeliveryEntity entity = requireByOrderId(orderId);
        entity.setState(DeliveryState.FAILED);
        deliveryRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal deliveryCost(OrderDto order) {
        DeliveryEntity entity = deliveryRepository.findByOrderId(order.orderId())
                .orElseThrow(() -> new NoDeliveryFoundException(
                        "No delivery planned for order: " + order.orderId()
                ));
        if (order.deliveryPrice() != null) {
            return order.deliveryPrice().setScale(2, RoundingMode.HALF_UP);
        }
        if (entity.getEstimatedCost() != null) {
            return entity.getEstimatedCost();
        }
        if (order.deliveryWeight() != null || order.deliveryVolume() != null) {
            double w = order.deliveryWeight() != null ? order.deliveryWeight() : 0;
            double v = order.deliveryVolume() != null ? order.deliveryVolume() : 0;
            return BigDecimal.valueOf(w * 15 + v * 5 + 300).setScale(2, RoundingMode.HALF_UP);
        }
        return DEFAULT_ROUTE_COST;
    }

    private DeliveryEntity requireByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("No delivery for order: " + orderId));
    }

    private static BigDecimal estimateRouteCost(DeliveryDto delivery) {
        AddressDto to = delivery.toAddress();
        if (to != null && to.city() != null && !to.city().isBlank()) {
            return DEFAULT_ROUTE_COST.add(
                    BigDecimal.valueOf(Math.min(to.city().length() * 12L, 500))
            ).setScale(2, RoundingMode.HALF_UP);
        }
        return DEFAULT_ROUTE_COST;
    }

    private static void applyAddresses(DeliveryEntity target, DeliveryDto dto) {
        AddressDto from = dto.fromAddress();
        if (from != null) {
            target.setFromCountry(from.country());
            target.setFromCity(from.city());
            target.setFromStreet(from.street());
            target.setFromHouse(from.house());
            target.setFromFlat(from.flat());
        }
        AddressDto to = dto.toAddress();
        if (to != null) {
            target.setToCountry(to.country());
            target.setToCity(to.city());
            target.setToStreet(to.street());
            target.setToHouse(to.house());
            target.setToFlat(to.flat());
        }
    }

    private static DeliveryDto toDto(DeliveryEntity entity) {
        AddressDto from = new AddressDto(
                entity.getFromCountry(),
                entity.getFromCity(),
                entity.getFromStreet(),
                entity.getFromHouse(),
                entity.getFromFlat()
        );
        AddressDto to = new AddressDto(
                entity.getToCountry(),
                entity.getToCity(),
                entity.getToStreet(),
                entity.getToHouse(),
                entity.getToFlat()
        );
        return new DeliveryDto(
                entity.getDeliveryId(),
                from,
                to,
                entity.getOrderId(),
                entity.getState()
        );
    }
}

package ru.yandex.practicum.commerce.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.order.OrderOperations;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.commerce.order.repository.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService implements OrderOperations {

    private static final BigDecimal STUB_UNIT_PRICE = new BigDecimal("100.00");
    private static final BigDecimal STUB_DELIVERY_PRICE = new BigDecimal("500.00");

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        requireUsername(username);
        return orderRepository.findByUsernameOrderByCreatedAtDesc(username.trim()).stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        var cart = request.shoppingCart();
        Map<UUID, Long> products = cart.products();
        if (products == null || products.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("No orderable products in shopping cart");
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(UUID.randomUUID());
        entity.setUsername(trimToNull(request.username()));
        entity.setShoppingCartId(cart.shoppingCartId());
        entity.setProducts(new HashMap<>(products));
        entity.setState(OrderState.NEW);
        entity.setFragile(false);
        entity.setDeliveryWeight(0.0);
        entity.setDeliveryVolume(0.0);
        applyAddress(entity, request.deliveryAddress());
        return OrderMapper.toDto(orderRepository.save(entity));
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        if (request.orderId() == null) {
            throw new NoOrderFoundException("orderId is required");
        }
        OrderEntity order = requireOrder(request.orderId());
        Map<UUID, Long> products = order.getProducts();
        for (Map.Entry<UUID, Long> entry : request.products().entrySet()) {
            UUID productId = entry.getKey();
            long toReturn = entry.getValue() != null ? entry.getValue() : 0L;
            if (toReturn <= 0) {
                continue;
            }
            Long current = products.get(productId);
            if (current == null) {
                throw new IllegalArgumentException("Product is not in the order: " + productId);
            }
            if (current < toReturn) {
                throw new IllegalArgumentException("Return quantity exceeds ordered quantity for " + productId);
            }
            long remaining = current - toReturn;
            if (remaining == 0) {
                products.remove(productId);
            } else {
                products.put(productId, remaining);
            }
        }
        order.setState(OrderState.PRODUCT_RETURNED);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setState(OrderState.PAID);
        if (order.getPaymentId() == null) {
            order.setPaymentId(UUID.randomUUID());
        }
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setState(OrderState.DELIVERED);
        if (order.getDeliveryId() == null) {
            order.setDeliveryId(UUID.randomUUID());
        }
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        BigDecimal sum = BigDecimal.ZERO;
        for (long qty : order.getProducts().values()) {
            sum = sum.add(STUB_UNIT_PRICE.multiply(BigDecimal.valueOf(qty)));
        }
        order.setProductPrice(sum);
        applyTotal(order);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setDeliveryPrice(STUB_DELIVERY_PRICE);
        applyTotal(order);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setState(OrderState.ASSEMBLED);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    private OrderEntity requireOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found: " + orderId));
    }

    private static void requireUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username must not be empty");
        }
    }

    private static void applyAddress(OrderEntity entity, AddressDto address) {
        if (address == null) {
            return;
        }
        entity.setCountry(address.country());
        entity.setCity(address.city());
        entity.setStreet(address.street());
        entity.setHouse(address.house());
        entity.setFlat(address.flat());
    }

    private static void applyTotal(OrderEntity order) {
        BigDecimal product = order.getProductPrice() != null ? order.getProductPrice() : BigDecimal.ZERO;
        BigDecimal delivery = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO;
        order.setTotalPrice(product.add(delivery));
    }

    private static String trimToNull(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return username.trim();
    }
}

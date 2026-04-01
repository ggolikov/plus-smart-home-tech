package ru.yandex.practicum.commerce.warehouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperations;
import ru.yandex.practicum.commerce.dto.shopping.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.config.WarehouseAddressProperties;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseProductRepository;
import ru.yandex.practicum.commerce.warehouse.repository.entity.WarehouseProductEntity;

import java.util.Map;
import java.util.UUID;

@Service
public class WarehouseService implements WarehouseOperations {

    private final WarehouseProductRepository warehouseProductRepository;
    private final WarehouseAddressProperties warehouseAddressProperties;

    public WarehouseService(
            WarehouseProductRepository warehouseProductRepository,
            WarehouseAddressProperties warehouseAddressProperties
    ) {
        this.warehouseProductRepository = warehouseProductRepository;
        this.warehouseAddressProperties = warehouseAddressProperties;
    }

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        UUID productId = request.productId();
        if (warehouseProductRepository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Product is already registered in the warehouse: " + productId
            );
        }
        WarehouseProductEntity entity = new WarehouseProductEntity();
        entity.setProductId(productId);
        entity.setFragile(Boolean.TRUE.equals(request.fragile()));
        entity.setWidth(request.dimension().width());
        entity.setHeight(request.dimension().height());
        entity.setDepth(request.dimension().depth());
        entity.setWeight(request.weight());
        entity.setQuantityOnWarehouse(0L);
        warehouseProductRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        Map<UUID, Long> products = shoppingCart.products();
        if (products == null || products.isEmpty()) {
            return new BookedProductsDto(0.0, 0.0, false);
        }
        double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            long requested = entry.getValue() != null ? entry.getValue() : 0L;
            if (requested <= 0) {
                continue;
            }
            WarehouseProductEntity w = warehouseProductRepository.findById(productId)
                    .orElseThrow(() -> new ProductInShoppingCartLowQuantityInWarehouseException(
                            "Product is not available on the warehouse: " + productId
                    ));
            if (w.getQuantityOnWarehouse() < requested) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(
                        "Insufficient quantity on warehouse for product " + productId
                                + ": need " + requested + ", have " + w.getQuantityOnWarehouse()
                );
            }
            deliveryWeight += requested * w.getWeight();
            deliveryVolume += requested * w.getWidth() * w.getHeight() * w.getDepth();
            fragile = fragile || w.isFragile();
        }
        return new BookedProductsDto(deliveryWeight, deliveryVolume, fragile);
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        UUID productId = request.productId();
        if (productId == null) {
            throw new NoSpecifiedProductInWarehouseException("productId is required");
        }
        WarehouseProductEntity entity = warehouseProductRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "No product information in the warehouse: " + productId
                ));
        entity.setQuantityOnWarehouse(entity.getQuantityOnWarehouse() + request.quantity());
        warehouseProductRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getWarehouseAddress() {
        return warehouseAddressProperties.toAddressDto();
    }
}

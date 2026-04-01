package ru.yandex.practicum.commerce.shoppingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperations;
import ru.yandex.practicum.commerce.dto.shopping.store.*;
import ru.yandex.practicum.commerce.shoppingstore.repository.ProductRepository;
import ru.yandex.practicum.commerce.shoppingstore.repository.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ShoppingStoreService implements ShoppingStoreOperations {

    private final ProductRepository productRepository;

    public ShoppingStoreService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageProductDto getProducts(
            ProductCategory category,
            int page,
            int size,
            List<String> sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Product> result = productRepository.findByProductCategory(category, pageable);
        return toPageProductDto(result);
    }

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto product) {
        Product entity = new Product();
        UUID id = product.productId() != null ? product.productId() : UUID.randomUUID();
        entity.setProductId(id);
        applyDto(entity, product);
        return toDto(productRepository.save(entity));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto product) {
        UUID id = product.productId();
        if (id == null) {
            throw new IllegalArgumentException("productId is required for update");
        }
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        applyDto(entity, product);
        return toDto(productRepository.save(entity));
    }

    @Override
    @Transactional
    public Boolean removeProductFromStore(UUID productId) {
        Product entity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        entity.setProductState(ProductState.DEACTIVATE);
        ProductDto productDto = toDto(entity);
        applyDto(entity, productDto);
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product entity = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));
        entity.setQuantityState(request.quantityState());
        productRepository.save(entity);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        Product entity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return toDto(entity);
    }

    private static Sort parseSort(List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();

        orders.add(new Sort.Order(Sort.Direction.fromString(sort.get(1).trim()), sort.get(0).trim()));

        return Sort.by(orders);
    }

    private PageProductDto toPageProductDto(Page<Product> page) {
        List<ProductDto> content = page.getContent().stream().map(this::toDto).toList();
        List<SortObject> sortObjects = page.getSort().stream()
                .map(ShoppingStoreService::toSortObject)
                .toList();
        Pageable pageable = page.getPageable();
        List<SortObject> pageableSort = pageable.getSort().stream()
                .map(ShoppingStoreService::toSortObject)
                .toList();
        PageableObject pageableObject = new PageableObject(
                pageable.getOffset(),
                pageableSort,
                pageable.isUnpaged(),
                pageable.isPaged(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
        return new PageProductDto(
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSize(),
                content,
                page.getNumber(),
                sortObjects,
                page.getNumberOfElements(),
                pageableObject,
                page.isEmpty()
        );
    }

    private static SortObject toSortObject(Sort.Order order) {
        return new SortObject(
                order.getDirection().name(),
                order.getNullHandling().name(),
                order.isAscending(),
                order.getProperty(),
                order.isIgnoreCase()
        );
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getImageSrc(),
                product.getQuantityState(),
                product.getProductState(),
                product.getProductCategory(),
                product.getPrice()
        );
    }

    private static void applyDto(Product target, ProductDto dto) {
        target.setProductName(dto.productName());
        target.setDescription(dto.description());
        target.setImageSrc(dto.imageSrc());
        target.setQuantityState(dto.quantityState());
        target.setProductState(dto.productState());
        target.setProductCategory(dto.productCategory());
        target.setPrice(dto.price());
    }
}

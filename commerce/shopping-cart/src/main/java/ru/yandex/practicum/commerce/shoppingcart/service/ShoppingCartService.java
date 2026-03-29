package ru.yandex.practicum.commerce.shoppingcart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.shopping.cart.ShoppingCartOperations;
import ru.yandex.practicum.commerce.dto.shopping.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.shopping.ShoppingCartDto;
import ru.yandex.practicum.commerce.shoppingcart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.shoppingcart.repository.entity.ShoppingCartEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ShoppingCartService implements ShoppingCartOperations {

    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        requireUsername(username);
        return toDto(getOrCreateCart(username));
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> productIdToQuantity) {
        requireUsername(username);
        ShoppingCartEntity cart = getOrCreateCart(username);
        Map<UUID, Long> products = cart.getProducts();
        if (productIdToQuantity != null) {
            for (Map.Entry<UUID, Long> entry : productIdToQuantity.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                products.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
        }
        shoppingCartRepository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        requireUsername(username);
        shoppingCartRepository.deleteByUsername(username.trim());
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        requireUsername(username);
        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username.trim())
                .orElseThrow(() -> new NoProductsInShoppingCartException("No shopping cart for user"));
        if (productIds == null || productIds.isEmpty()) {
            throw new NoProductsInShoppingCartException("No product ids to remove");
        }
        Map<UUID, Long> products = cart.getProducts();
        long removed = productIds.stream()
                .filter(id -> id != null && products.remove(id) != null)
                .count();
        if (removed == 0) {
            throw new NoProductsInShoppingCartException("None of the requested products are in the cart");
        }
        shoppingCartRepository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        requireUsername(username);
        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username.trim())
                .orElseThrow(() -> new NoProductsInShoppingCartException("No shopping cart for user"));
        UUID productId = request.productId();
        if (!cart.getProducts().containsKey(productId)) {
            throw new NoProductsInShoppingCartException("Product is not in the cart");
        }
        cart.getProducts().put(productId, request.newQuantity());
        shoppingCartRepository.save(cart);
        return toDto(cart);
    }

    private static void requireUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username must not be empty");
        }
    }

    private ShoppingCartEntity getOrCreateCart(String username) {
        String key = username.trim();
        return shoppingCartRepository.findByUsername(key)
                .orElseGet(() -> {
                    ShoppingCartEntity entity = new ShoppingCartEntity();
                    entity.setShoppingCartId(UUID.randomUUID());
                    entity.setUsername(key);
                    entity.setProducts(new HashMap<>());
                    return shoppingCartRepository.save(entity);
                });
    }

    private static ShoppingCartDto toDto(ShoppingCartEntity cart) {
        return new ShoppingCartDto(cart.getShoppingCartId(), new HashMap<>(cart.getProducts()));
    }
}

package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.CartItemDto;
import com.meldateksari.eticaret.model.Cart;
import com.meldateksari.eticaret.model.CartItem;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.repository.CartItemRepository;
import com.meldateksari.eticaret.repository.CartRepository;
import com.meldateksari.eticaret.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartItemService(CartItemRepository cartItemRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public CartItem addOrUpdateCartItem(Long cartId, Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .map(existingItem -> {
                    existingItem.setQuantity(quantity);
                    return cartItemRepository.save(existingItem);
                })
                .orElseGet(() -> {
                    CartItem newItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(quantity)
                            .build();

                    cart.getItems().add(newItem); // ✅ Burası kritik
                    return cartItemRepository.save(newItem);
                });
    }


    public List<CartItem> getCartItems(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
    public void removeItem(Long cartItemId) {
        if (cartItemRepository.existsById(cartItemId)) {
            cartItemRepository.deleteById(cartItemId);
        } else {
            throw new EntityNotFoundException("CartItem bulunamadı: ID=" + cartItemId);
        }
    }
    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }


    public List<CartItemDto> getAllItems() {
        return cartItemRepository.findAll()
                .stream()
                .map(item -> new CartItemDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getProduct().getPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());
    }
}

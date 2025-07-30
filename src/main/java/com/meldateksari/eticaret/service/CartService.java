package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.Cart;
import com.meldateksari.eticaret.model.CartItem;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.CartRepository;
import com.meldateksari.eticaret.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    public Cart createCart(Long userId) {
        if (cartRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User already has a cart.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Cart cart = Cart.builder()
                .user(user)
                .build();

        return cartRepository.save(cart);
    }

    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }



}


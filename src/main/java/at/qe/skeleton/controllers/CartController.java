package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.CartDTO;
import at.qe.skeleton.dtos.CartItemCreateDTO;
import at.qe.skeleton.dtos.CartItemUpdateDTO;
import at.qe.skeleton.mappers.CartMapper;
import at.qe.skeleton.model.Cart;
import at.qe.skeleton.services.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

 
    @GetMapping("")
    public ResponseEntity<CartDTO> getCart() {
        Cart cart = cartService.getCart();
        CartDTO cartDTO = cartMapper.mapTo(cart);
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(@Valid @RequestBody CartItemCreateDTO createDTO) {
        Cart cart = cartService.addItemToCart(createDTO);
        CartDTO cartDTO = cartMapper.mapTo(cart);
        return ResponseEntity.ok(cartDTO);
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody CartItemUpdateDTO updateDTO) {
        Cart cart = cartService.updateCartItem(id, updateDTO);
        CartDTO cartDTO = cartMapper.mapTo(cart);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}


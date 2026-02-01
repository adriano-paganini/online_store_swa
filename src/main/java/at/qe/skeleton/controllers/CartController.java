package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.CartDTO;
import at.qe.skeleton.dtos.CartItemCreateDTO;
import at.qe.skeleton.dtos.CartItemUpdateDTO;
import at.qe.skeleton.mappers.CartItemCreateMapper;
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

/**
 * REST controller for managing the authenticated user's shopping cart.
 *
 * <p>
 * All endpoints operate on the cart of the currently authenticated user
 * and allow adding, updating, and removing cart items.
 * </p>
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final CartItemCreateMapper cartItemCreateMapper;

    public CartController(CartService cartService, CartMapper cartMapper, CartItemCreateMapper cartItemCreateMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
        this.cartItemCreateMapper = cartItemCreateMapper;
    }

    /**
     * Retrieves the current user's shopping cart.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - cart successfully retrieved</li>
     * </ul>
     *
     * @return the current cart
     */
    @GetMapping("")
    public ResponseEntity<CartDTO> getCart() {
        Cart cart = cartService.getCart();
        CartDTO cartDTO = cartMapper.mapTo(cart);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * Adds an item to the current user's cart.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - item added to cart</li>
     *   <li>400 Bad Request - product unavailable or insufficient stock</li>
     *   <li>404 Not Found - product does not exist</li>
     * </ul>
     *
     * @param createDTO item data to add
     * @return the updated cart
     */
    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(@Valid @RequestBody CartItemCreateDTO createDTO) {
        var cartItem = cartItemCreateMapper.mapFrom(createDTO);
        Cart cart = cartService.addItemToCart(cartItem);
        CartDTO cartDTO = cartMapper.mapTo(cart);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * Updates the quantity or discount of a cart item.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - cart item updated</li>
     *   <li>400 Bad Request - invalid quantity or insufficient stock</li>
     *   <li>404 Not Found - cart item does not exist</li>
     * </ul>
     *
     * @param id identifier of the cart item
     * @param updateDTO updated cart item data
     * @return the updated cart
     */
    @PatchMapping("/items/{id}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody CartItemUpdateDTO updateDTO) {
        Cart cart = cartService.updateCartItem(id, updateDTO);
        CartDTO cartDTO = cartMapper.mapTo(cart);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * Removes an item from the cart.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>204 No Content - item removed</li>
     *   <li>404 Not Found - cart item does not exist</li>
     * </ul>
     *
     * @param id identifier of the cart item
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes all items from the cart.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>204 No Content - cart cleared</li>
     * </ul>
     */
    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}


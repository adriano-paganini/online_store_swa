package at.qe.skeleton.services;

import at.qe.skeleton.dtos.CartItemUpdateDTO;
import at.qe.skeleton.mappers.CartMapper;
import at.qe.skeleton.model.Cart;
import at.qe.skeleton.model.CartItem;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.CartItemRepository;
import at.qe.skeleton.repositories.CartRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ProductService productService;
    private final CartMapper cartMapper;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            AuthenticatedUserService authenticatedUserService,
            ProductService productService,
            CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.productService = productService;
        this.cartMapper = cartMapper;
    }

    @Transactional
    public Cart getOrCreateCart() {
        Userx user = authenticatedUserService.getAuthenticatedUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        if (cartOpt.isPresent()) {
            return cartOpt.get();
        }

        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Cart getCart() {
        Userx user = authenticatedUserService.getAuthenticatedUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setItems(List.of());
                    return cart;
                });
    }

    @Transactional
    public Cart addItemToCart(CartItem cartItem) {
        if (!productService.isProductAvailable(cartItem.getProductId(), cartItem.getQuantity())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not available or insufficient stock");
        }

        Cart cart = getOrCreateCart();
        cartItem.setCart(cart);

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProductId(cart, cartItem.getProductId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
            
            if (!productService.isProductAvailable(cartItem.getProductId(), newQuantity)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
            }
            
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            Double productPrice = productService.getProductPrice(cartItem.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            Double productDiscount = productService.getProductDiscount(cartItem.getProductId())
                    .orElse(0.0);

            cartItem.setCurrentPrice(productPrice);
            validateDiscount(productDiscount);
            cartItem.setAppliedDiscount(productDiscount > 0 ? productDiscount : null);

            cart.getItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        return cartRepository.save(cart);
    }

    private void validateDiscount(Double discount) {
        if (discount == null) return;

        if (discount < 0.0 || discount > 1.0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Discount must be between 0.00 and 1.00"
            );
        }
    }

    @Transactional
    public Cart updateCartItem(Long itemId, CartItemUpdateDTO updateDTO) {
        Cart cart = getCart();
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart item does not belong to user");
        }

        // Validate DTO before applying changes (service can access DTO to keep controller thin)
        if (updateDTO.quantity() != null) {
            if (updateDTO.quantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
            }
            
            if (!productService.isProductAvailable(item.getProductId(), updateDTO.quantity())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
            }
        }

        // Apply DTO changes via mapper
        cartMapper.apply(item, updateDTO);
        cartItemRepository.save(item);
        return cartRepository.save(cart);
    }

    @Transactional
    public void removeCartItem(Long itemId) {
        Cart cart = getCart();
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart item does not belong to user");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart() {
        Cart cart = getCart();
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}


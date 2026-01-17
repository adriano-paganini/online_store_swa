package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.CartItemCreateDTO;
import at.qe.skeleton.dtos.CartItemUpdateDTO;
import at.qe.skeleton.model.Cart;
import at.qe.skeleton.model.CartItem;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.CartItemRepository;
import at.qe.skeleton.repositories.CartRepository;
import at.qe.skeleton.services.AuthenticatedUserService;
import at.qe.skeleton.services.CartService;
import at.qe.skeleton.services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@SpringBootTest
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private ProductService productService;

    private Userx testUser;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());

        Mockito.when(authenticatedUserService.getAuthenticatedUser()).thenReturn(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetCartExisting() {
        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getCart();

        Assertions.assertNotNull(result, "Cart should not be null");
        Assertions.assertEquals(testCart.getId(), result.getId(), "Cart ID should match");
        Mockito.verify(cartRepository).findByUser(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetCartNotExisting() {
        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());

        Cart result = cartService.getCart();

        Assertions.assertNotNull(result, "Cart should not be null");
        Assertions.assertNull(result.getId(), "Cart should not have an ID yet");
        Assertions.assertEquals(testUser, result.getUser(), "Cart should be associated with user");
        Assertions.assertTrue(result.getItems().isEmpty(), "Cart should have empty items list");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetOrCreateCartExisting() {
        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getOrCreateCart();

        Assertions.assertNotNull(result, "Cart should not be null");
        Assertions.assertEquals(testCart.getId(), result.getId(), "Cart ID should match");
        Mockito.verify(cartRepository).findByUser(testUser);
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any(Cart.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetOrCreateCartNotExisting() {
        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(1L);
            return cart;
        });

        Cart result = cartService.getOrCreateCart();

        Assertions.assertNotNull(result, "Cart should not be null");
        Assertions.assertEquals(testUser, result.getUser(), "Cart should be associated with user");
        Mockito.verify(cartRepository).save(Mockito.any(Cart.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddItemToCartNewItem() {
        Long productId = 10L;
        Integer quantity = 2;
        Double productPrice = 29.99;
        Double productDiscount = 0.0;

        CartItem cartItem = new CartItem();
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartItemRepository.findByCartAndProductId(testCart, productId))
                .thenReturn(Optional.empty());
        Mockito.when(productService.isProductAvailable(productId, quantity)).thenReturn(true);
        Mockito.when(productService.getProductPrice(productId)).thenReturn(Optional.of(productPrice));
        Mockito.when(productService.getProductDiscount(productId)).thenReturn(Optional.of(productDiscount));
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(testCart);
        Mockito.when(cartItemRepository.save(Mockito.any(CartItem.class))).thenAnswer(invocation -> {
            CartItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        Cart result = cartService.addItemToCart(cartItem);

        Assertions.assertNotNull(result, "Cart should not be null");
        ArgumentCaptor<CartItem> itemCaptor = ArgumentCaptor.forClass(CartItem.class);
        Mockito.verify(cartItemRepository).save(itemCaptor.capture());
        CartItem savedItem = itemCaptor.getValue();
        Assertions.assertEquals(productId, savedItem.getProductId(), "Product ID should match");
        Assertions.assertEquals(quantity, savedItem.getQuantity(), "Quantity should match");
        Assertions.assertEquals(productPrice, savedItem.getCurrentPrice(), "Price should match");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddItemToCartExistingItem() {
        Long productId = 10L;
        Integer existingQuantity = 2;
        Integer addQuantity = 1;
        Integer expectedQuantity = existingQuantity + addQuantity;

        CartItem cartItem = new CartItem();
        cartItem.setProductId(productId);
        cartItem.setQuantity(addQuantity);

        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProductId(productId);
        existingItem.setQuantity(existingQuantity);
        existingItem.setCurrentPrice(29.99);
        testCart.setItems(List.of(existingItem));

        Mockito.when(productService.isProductAvailable(productId, addQuantity)).thenReturn(true);
        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartItemRepository.findByCartAndProductId(testCart, productId))
                .thenReturn(Optional.of(existingItem));
        Mockito.when(productService.isProductAvailable(productId, expectedQuantity)).thenReturn(true);
        Mockito.when(cartItemRepository.save(Mockito.any(CartItem.class))).thenReturn(existingItem);
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.addItemToCart(cartItem);

        Assertions.assertNotNull(result, "Cart should not be null");
        Assertions.assertEquals(expectedQuantity, existingItem.getQuantity(), "Quantity should be incremented");
        Mockito.verify(cartItemRepository).save(existingItem);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddItemToCartProductNotAvailable() {
        Long productId = 10L;
        Integer quantity = 2;
        CartItem cartItem = new CartItem();
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(productService.isProductAvailable(productId, quantity)).thenReturn(false);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            cartService.addItemToCart(cartItem);
        }, "Should throw exception when product is not available");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateCartItem() {
        Long itemId = 1L;
        Integer newQuantity = 5;
        CartItemUpdateDTO updateDTO = new CartItemUpdateDTO(newQuantity, null);

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setCart(testCart);
        item.setProductId(10L);
        item.setQuantity(2);
        item.setCurrentPrice(29.99);
        testCart.setItems(List.of(item));

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(productService.isProductAvailable(item.getProductId(), newQuantity)).thenReturn(true);
        Mockito.when(cartItemRepository.save(Mockito.any(CartItem.class))).thenReturn(item);
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.updateCartItem(itemId, updateDTO);

        Assertions.assertNotNull(result, "Cart should not be null");
        Assertions.assertEquals(newQuantity, item.getQuantity(), "Quantity should be updated");
        Mockito.verify(cartItemRepository).save(item);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateCartItemNotFound() {
        Long itemId = 999L;
        CartItemUpdateDTO updateDTO = new CartItemUpdateDTO(3, null);

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartItemRepository.findById(itemId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            cartService.updateCartItem(itemId, updateDTO);
        }, "Should throw exception when cart item not found");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateCartItemWrongUser() {
        Long itemId = 1L;
        CartItemUpdateDTO updateDTO = new CartItemUpdateDTO(3, null);

        Cart otherCart = new Cart();
        otherCart.setId(999L);

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setCart(otherCart);
        item.setProductId(10L);
        item.setQuantity(2);

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            cartService.updateCartItem(itemId, updateDTO);
        }, "Should throw exception when cart item belongs to different user");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateCartItemInvalidQuantity() {
        Long itemId = 1L;
        CartItemUpdateDTO updateDTO = new CartItemUpdateDTO(0, null);

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setCart(testCart);
        item.setProductId(10L);
        item.setQuantity(2);

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            cartService.updateCartItem(itemId, updateDTO);
        }, "Should throw exception when quantity is invalid");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRemoveCartItem() {
        Long itemId = 1L;

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setCart(testCart);
        item.setProductId(10L);
        item.setQuantity(2);
        testCart.setItems(new ArrayList<>(List.of(item)));

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.doNothing().when(cartItemRepository).delete(item);
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(testCart);

        cartService.removeCartItem(itemId);

        Mockito.verify(cartItemRepository).delete(item);
        Mockito.verify(cartRepository).save(testCart);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRemoveCartItemNotFound() {
        Long itemId = 999L;

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartItemRepository.findById(itemId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            cartService.removeCartItem(itemId);
        }, "Should throw exception when cart item not found");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testClearCart() {
        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setCart(testCart);
        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setCart(testCart);
        testCart.setItems(new ArrayList<>(List.of(item1, item2)));

        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(testCart);

        cartService.clearCart();

        Assertions.assertTrue(testCart.getItems().isEmpty(), "Cart items should be cleared");
        Mockito.verify(cartRepository).save(testCart);
    }

    @Test
    void testGetCartUnauthenticated() {
        Mockito.when(authenticatedUserService.getAuthenticatedUser()).thenReturn(null);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            cartService.getCart();
        }, "Should throw exception when user is not authenticated");
    }

    @Test
    void testAddItemToCartUnauthenticated() {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(10L);
        cartItem.setQuantity(1);
        Mockito.when(authenticatedUserService.getAuthenticatedUser()).thenReturn(null);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            cartService.addItemToCart(cartItem);
        }, "Should throw exception when user is not authenticated");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testConcurrentCartModification() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setStock(100);
        product.setDeleted(false);

        Mockito.when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        Mockito.when(productService.getProductPrice(1L)).thenReturn(Optional.of(100.0));
        Mockito.when(productService.getProductDiscount(1L)).thenReturn(Optional.of(0.1));
        Mockito.when(productService.isProductAvailable(1L, Mockito.anyInt())).thenReturn(true);

        CartItem item1 = new CartItem();
        item1.setProductId(1L);
        item1.setQuantity(1);
        item1.setCurrentPrice(100.0);
        item1.setAppliedDiscount(0.1);

        testCart.setItems(new ArrayList<>(List.of(item1)));
        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(cartItemRepository.findByCartAndProductId(Mockito.any(Cart.class), Mockito.eq(1L)))
                .thenReturn(Optional.of(item1));
        Mockito.when(cartItemRepository.save(Mockito.any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Simulate concurrent modification: add item while cart is being modified
        CartItem newItem = new CartItem();
        newItem.setProductId(1L);
        newItem.setQuantity(1);
        Cart result1 = cartService.addItemToCart(newItem);

        // Verify cart state is consistent
        Assertions.assertNotNull(result1);
        Assertions.assertNotNull(result1.getItems());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCartWithMultipleConcurrentAdds() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setStock(100);
        product.setDeleted(false);

        Mockito.when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        Mockito.when(productService.getProductPrice(1L)).thenReturn(Optional.of(100.0));
        Mockito.when(productService.getProductDiscount(1L)).thenReturn(Optional.of(0.1));
        Mockito.when(productService.isProductAvailable(1L, Mockito.anyInt())).thenReturn(true);

        CartItem item1 = new CartItem();
        item1.setProductId(1L);
        item1.setQuantity(1);

        testCart.setItems(new ArrayList<>());
        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(cartItemRepository.findByCartAndProductId(Mockito.any(Cart.class), Mockito.eq(1L)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(item1));
        Mockito.when(cartItemRepository.save(Mockito.any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // First add
        Cart result1 = cartService.addItemToCart(item1);
        Assertions.assertEquals(1, result1.getItems().size());

        // Second add to same product (should update quantity)
        CartItem item2 = new CartItem();
        item2.setProductId(1L);
        item2.setQuantity(2);
        Cart result2 = cartService.addItemToCart(item2);

        // Should have one item with updated quantity
        Assertions.assertEquals(1, result2.getItems().size());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCartClearRemovesAllItems() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setStock(100);
        product.setDeleted(false);

        Mockito.when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        Mockito.when(productService.getProductPrice(1L)).thenReturn(Optional.of(100.0));
        Mockito.when(productService.getProductDiscount(1L)).thenReturn(Optional.of(0.1));
        Mockito.when(productService.isProductAvailable(1L, Mockito.anyInt())).thenReturn(true);

        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setProductId(1L);
        item1.setQuantity(1);
        item1.setCurrentPrice(100.0);
        item1.setAppliedDiscount(0.1);

        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setProductId(1L);
        item2.setQuantity(2);
        item2.setCurrentPrice(100.0);
        item2.setAppliedDiscount(0.1);

        testCart.setItems(new ArrayList<>(List.of(item1, item2)));
        Mockito.when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.clearCart();

        Assertions.assertTrue(testCart.getItems().isEmpty(), "Cart should be empty after clearing");
        Mockito.verify(cartRepository).save(testCart);
    }
}


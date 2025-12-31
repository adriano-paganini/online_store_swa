package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.configs.TokenAuthenticationFilter;
import at.qe.skeleton.controllers.CartController;
import at.qe.skeleton.dtos.CartDTO;
import at.qe.skeleton.dtos.CartItemCreateDTO;
import at.qe.skeleton.dtos.CartItemDTO;
import at.qe.skeleton.dtos.CartItemUpdateDTO;
import at.qe.skeleton.mappers.CartMapper;
import at.qe.skeleton.model.Cart;
import at.qe.skeleton.model.CartItem;
import at.qe.skeleton.services.CartService;
import at.qe.skeleton.services.UserxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@WebMvcTest(CartController.class)
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoSpyBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @MockitoBean
    private JwtConfig jwtConfig;

    @MockitoBean
    private UserxService userService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CartMapper cartMapper;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(tokenAuthenticationFilter).doFilterInternal(
                Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class),
                Mockito.any(FilterChain.class)
        );

        @SuppressWarnings("unchecked")
        Jws<Claims> mockJws = (Jws<Claims>) Mockito.mock(Jws.class);
        Claims mockClaims = Mockito.mock(Claims.class);
        Mockito.when(mockClaims.getSubject()).thenReturn("user1");
        Mockito.when(mockJws.getPayload()).thenReturn(mockClaims);
        Mockito.when(jwtTokenProvider.validateTokenAndGetJws(Mockito.anyString()))
                .thenReturn(Optional.of(mockJws));
    }

    @Test
    @WithMockUser(username = "user1")
    void getCart() throws Exception {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());

        CartDTO cartDTO = new CartDTO(new ArrayList<>());

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(cartMapper.mapTo(cart)).thenReturn(cartDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isEmpty());
    }

    @Test
    @WithMockUser(username = "user1")
    void getCartWithItems() throws Exception {
        Cart cart = new Cart();
        cart.setId(1L);

        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setProductId(10L);
        item1.setQuantity(2);
        item1.setCurrentPrice(29.99);
        item1.setAppliedDiscount(0.1);

        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setProductId(20L);
        item2.setQuantity(1);
        item2.setCurrentPrice(49.99);
        item2.setAppliedDiscount(null);

        cart.setItems(List.of(item1, item2));

        CartItemDTO itemDTO1 = new CartItemDTO(1L, 10L, 2, 0.1, 29.99);
        CartItemDTO itemDTO2 = new CartItemDTO(2L, 20L, 1, null, 49.99);
        CartDTO cartDTO = new CartDTO(List.of(itemDTO1, itemDTO2));

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(cartMapper.mapTo(cart)).thenReturn(cartDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].productId").value(10L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].quantity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].appliedDiscount").value(0.1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].currentPrice").value(29.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].productId").value(20L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].quantity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].appliedDiscount").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].currentPrice").value(49.99));
    }

    @Test
    @WithMockUser(username = "user1")
    void addItemToCart() throws Exception {
        CartItemCreateDTO createDTO = new CartItemCreateDTO(10L, 2);

        Cart cart = new Cart();
        cart.setId(1L);
        CartItem item = new CartItem();
        item.setId(1L);
        item.setProductId(10L);
        item.setQuantity(2);
        item.setCurrentPrice(29.99);
        item.setAppliedDiscount(null);
        cart.setItems(List.of(item));

        CartItemDTO itemDTO = new CartItemDTO(1L, 10L, 2, null, 29.99);
        CartDTO cartDTO = new CartDTO(List.of(itemDTO));

        Mockito.when(cartService.addItemToCart(createDTO)).thenReturn(cart);
        Mockito.when(cartMapper.mapTo(cart)).thenReturn(cartDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/items")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].productId").value(10L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    @WithMockUser(username = "user1")
    void addItemToCartInvalidInput() throws Exception {
        // Missing productId
        CartItemCreateDTO invalidDTO = new CartItemCreateDTO(null, 2);

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/items")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1")
    void updateCartItem() throws Exception {
        Long itemId = 1L;
        CartItemUpdateDTO updateDTO = new CartItemUpdateDTO(3, null);

        Cart cart = new Cart();
        cart.setId(1L);
        CartItem item = new CartItem();
        item.setId(itemId);
        item.setProductId(10L);
        item.setQuantity(3);
        item.setCurrentPrice(29.99);
        cart.setItems(List.of(item));

        CartItemDTO itemDTO = new CartItemDTO(itemId, 10L, 3, null, 29.99);
        CartDTO cartDTO = new CartDTO(List.of(itemDTO));

        Mockito.when(cartService.updateCartItem(itemId, updateDTO)).thenReturn(cart);
        Mockito.when(cartMapper.mapTo(cart)).thenReturn(cartDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch("/cart/items/{id}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].quantity").value(3));
    }

    @Test
    @WithMockUser(username = "user1")
    void updateCartItemWithDiscount() throws Exception {
        Long itemId = 1L;
        CartItemUpdateDTO updateDTO = new CartItemUpdateDTO(null, 0.15);

        Cart cart = new Cart();
        cart.setId(1L);
        CartItem item = new CartItem();
        item.setId(itemId);
        item.setProductId(10L);
        item.setQuantity(2);
        item.setCurrentPrice(29.99);
        item.setAppliedDiscount(0.15);
        cart.setItems(List.of(item));

        CartItemDTO itemDTO = new CartItemDTO(itemId, 10L, 2, 0.15, 29.99);
        CartDTO cartDTO = new CartDTO(List.of(itemDTO));

        Mockito.when(cartService.updateCartItem(itemId, updateDTO)).thenReturn(cart);
        Mockito.when(cartMapper.mapTo(cart)).thenReturn(cartDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch("/cart/items/{id}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].appliedDiscount").value(0.15));
    }

    @Test
    @WithMockUser(username = "user1")
    void updateCartItemInvalidQuantity() throws Exception {
        Long itemId = 1L;
        CartItemUpdateDTO updateDTO = new CartItemUpdateDTO(0, null); // Invalid: quantity must be >= 1

        mockMvc.perform(MockMvcRequestBuilders.patch("/cart/items/{id}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1")
    void removeCartItem() throws Exception {
        Long itemId = 1L;

        Mockito.doNothing().when(cartService).removeCartItem(itemId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/items/{id}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(cartService).removeCartItem(itemId);
    }

    @Test
    @WithMockUser(username = "user1")
    void clearCart() throws Exception {
        Mockito.doNothing().when(cartService).clearCart();

        mockMvc.perform(MockMvcRequestBuilders.delete("/cart/items")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(cartService).clearCart();
    }

    @Test
    void getCartUnauthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cart"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void addItemToCartUnauthenticated() throws Exception {
        CartItemCreateDTO createDTO = new CartItemCreateDTO(10L, 1);

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/items")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}


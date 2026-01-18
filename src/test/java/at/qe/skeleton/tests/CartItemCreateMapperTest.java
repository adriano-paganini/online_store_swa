package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.CartItemCreateDTO;
import at.qe.skeleton.mappers.CartItemCreateMapper;
import at.qe.skeleton.model.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartItemCreateMapperTest {

    private CartItemCreateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CartItemCreateMapper();
    }

    @Test
    void mapFromWithCompleteDTO() {
        CartItemCreateDTO dto = new CartItemCreateDTO(1L, 5);

        CartItem cartItem = mapper.mapFrom(dto);

        assertNotNull(cartItem);
        assertEquals(1L, cartItem.getProductId());
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void mapFromWithNullDTO() {
        CartItem cartItem = mapper.mapFrom(null);

        assertNull(cartItem);
    }

    @Test
    void mapFromWithZeroQuantity() {
        CartItemCreateDTO dto = new CartItemCreateDTO(1L, 0);

        CartItem cartItem = mapper.mapFrom(dto);

        assertNotNull(cartItem);
        assertEquals(0, cartItem.getQuantity());
    }

    @Test
    void mapFromWithLargeQuantity() {
        CartItemCreateDTO dto = new CartItemCreateDTO(1L, 1000);

        CartItem cartItem = mapper.mapFrom(dto);

        assertNotNull(cartItem);
        assertEquals(1000, cartItem.getQuantity());
    }

    @Test
    void mapFromWithNegativeQuantity() {
        CartItemCreateDTO dto = new CartItemCreateDTO(1L, -5);

        CartItem cartItem = mapper.mapFrom(dto);

        assertNotNull(cartItem);
        assertEquals(-5, cartItem.getQuantity(), "Mapper should preserve negative quantity (validation elsewhere)");
    }

    @Test
    void mapFromWithNullProductId() {
        CartItemCreateDTO dto = new CartItemCreateDTO(null, 5);

        CartItem cartItem = mapper.mapFrom(dto);

        assertNotNull(cartItem);
        assertNull(cartItem.getProductId());
    }

    @Test
    void mapFromWithNullQuantity() {
        CartItemCreateDTO dto = new CartItemCreateDTO(1L, null);

        CartItem cartItem = mapper.mapFrom(dto);

        assertNotNull(cartItem);
        assertNull(cartItem.getQuantity());
    }

    @Test
    void mapFromDoesNotSetPriceOrDiscount() {
        CartItemCreateDTO dto = new CartItemCreateDTO(1L, 5);

        CartItem cartItem = mapper.mapFrom(dto);

        assertNotNull(cartItem);
        assertNull(cartItem.getCurrentPrice(), "Price should not be set by mapper");
        assertNull(cartItem.getAppliedDiscount(), "Discount should not be set by mapper");
    }
}

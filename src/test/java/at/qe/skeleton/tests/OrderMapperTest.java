package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.AddressDTO;
import at.qe.skeleton.dtos.OrderDTO;
import at.qe.skeleton.dtos.OrderItemDTO;
import at.qe.skeleton.mappers.OrderMapper;
import at.qe.skeleton.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderMapperTest {

    private OrderMapper orderMapper;
    private Order testOrder;
    private Userx testUser;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();

        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        OrderAddress shippingAddress = new OrderAddress(
                "Austria",
                "Innsbruck",
                "6020",
                "Test Street",
                "123",
                "RR21"
        );

        OrderAddress billingAddress = new OrderAddress(
                "Austria",
                "Vienna",
                "1010",
                "Billing Street",
                "456",
                null
        );

        OrderItem item1 = new OrderItem();
        item1.setProductId(1L);
        item1.setProductName("Product 1");
        item1.setQuantity(2);
        item1.setPriceAtPurchase(100.0);
        item1.setAppliedDiscount(10.0);

        OrderItem item2 = new OrderItem();
        item2.setProductId(2L);
        item2.setProductName("Product 2");
        item2.setQuantity(1);
        item2.setPriceAtPurchase(50.0);
        item2.setAppliedDiscount(null);

        testOrder = new Order(
                testUser,
                List.of(item1, item2),
                billingAddress,
                shippingAddress,
                ShippingMethod.FAIRY_DUST_DISPATCH,
                240.0
        );
        testOrder.setOrderNumber("ORD-12345");
        testOrder.setStatus(OrderStatus.PAID);
        
        // Set timestamp using reflection
        try {
            java.lang.reflect.Field timestampField = Order.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(testOrder, LocalDateTime.of(2024, 1, 15, 10, 30));
        } catch (Exception e) {
            // Ignore if reflection fails
        }
    }

    @Test
    void toDtoWithCompleteOrder() {
        OrderDTO dto = orderMapper.toDto(testOrder);

        assertNotNull(dto);
        assertEquals("ORD-12345", dto.orderNumber());
        assertEquals(OrderStatus.PAID, dto.status());
        assertEquals(240.0, dto.total());
        assertNotNull(dto.timestamp());
        assertEquals(2, dto.items().size());
        assertNotNull(dto.shippingAddress());
        assertNotNull(dto.billingAddress());
    }

    @Test
    void toDtoWithNullShippingAddress() {
        testOrder.setShippingAddress(null);
        OrderDTO dto = orderMapper.toDto(testOrder);

        assertNotNull(dto);
        assertNull(dto.shippingAddress());
        assertNotNull(dto.billingAddress());
    }

    @Test
    void toDtoWithNullBillingAddress() {
        testOrder.setBillingAddress(null);
        OrderDTO dto = orderMapper.toDto(testOrder);

        assertNotNull(dto);
        assertNotNull(dto.shippingAddress());
        assertNull(dto.billingAddress());
    }

    @Test
    void toDtoWithNullItems() {
        testOrder.setItems(null);
        
        // The mapper will throw NPE when trying to stream null items
        assertThrows(NullPointerException.class, () -> {
            orderMapper.toDto(testOrder);
        });
    }

    @Test
    void toDtoWithEmptyItems() {
        testOrder.setItems(new ArrayList<>());
        OrderDTO dto = orderMapper.toDto(testOrder);

        assertNotNull(dto);
        assertNotNull(dto.items());
        assertTrue(dto.items().isEmpty());
    }

    @Test
    void toDtoMapsOrderItemsCorrectly() {
        OrderDTO dto = orderMapper.toDto(testOrder);

        assertNotNull(dto.items());
        assertEquals(2, dto.items().size());

        OrderItemDTO item1 = dto.items().get(0);
        assertEquals(1L, item1.productId());
        assertEquals(2, item1.quantity());
        assertEquals(100.0, item1.priceAtPurchase());
        assertEquals(10.0, item1.appliedDiscount());

        OrderItemDTO item2 = dto.items().get(1);
        assertEquals(2L, item2.productId());
        assertEquals(1, item2.quantity());
        assertEquals(50.0, item2.priceAtPurchase());
        assertNull(item2.appliedDiscount());
    }

    @Test
    void toDtoMapsAddressesCorrectly() {
        OrderDTO dto = orderMapper.toDto(testOrder);

        AddressDTO shipping = dto.shippingAddress();
        assertNotNull(shipping);
        assertEquals("Austria", shipping.country());
        assertEquals("Innsbruck", shipping.city());
        assertEquals("6020", shipping.postalCode());
        assertEquals("Test Street", shipping.street());
        assertEquals("123", shipping.number());
        assertEquals("RR21", shipping.extra());

        AddressDTO billing = dto.billingAddress();
        assertNotNull(billing);
        assertEquals("Austria", billing.country());
        assertEquals("Vienna", billing.city());
        assertEquals("1010", billing.postalCode());
        assertEquals("Billing Street", billing.street());
        assertEquals("456", billing.number());
        assertNull(billing.extra());
    }

    @Test
    void toDtoWithItemNullDiscount() {
        OrderItem item = new OrderItem();
        item.setProductId(3L);
        item.setQuantity(1);
        item.setPriceAtPurchase(75.0);
        item.setAppliedDiscount(null);

        testOrder.setItems(List.of(item));
        OrderDTO dto = orderMapper.toDto(testOrder);

        assertNotNull(dto.items());
        assertEquals(1, dto.items().size());
        assertNull(dto.items().get(0).appliedDiscount());
    }

    @Test
    void toDtoWithAllOrderStatuses() {
        for (OrderStatus status : OrderStatus.values()) {
            testOrder.setStatus(status);
            OrderDTO dto = orderMapper.toDto(testOrder);

            assertNotNull(dto);
            assertEquals(status, dto.status());
        }
    }
}

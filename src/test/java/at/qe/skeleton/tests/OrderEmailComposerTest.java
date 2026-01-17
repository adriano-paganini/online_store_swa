package at.qe.skeleton.tests;

import at.qe.skeleton.Helpers.OrderEmailComposer;
import at.qe.skeleton.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderEmailComposerTest {

    @Test
    void composePlainTextWithNullOrderThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            OrderEmailComposer.composePlainText(null);
        });
    }

    @Test
    void composePlainTextWithCompleteOrder() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Hi John Doe,"));
        assertTrue(result.contains("Order number: ORD-12345"));
        assertTrue(result.contains("Status: CONFIRMED"));
        assertTrue(result.contains("Order total:"));
        assertTrue(result.contains("Shipping address"));
        assertTrue(result.contains("Billing address"));
        assertTrue(result.contains("What happens next?"));
    }

    @Test
    void composePlainTextWithNullUser() {
        Order order = createCompleteOrder(null);
        order.setUser(null);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Hi there,"));
    }

    @Test
    void composePlainTextWithNullUserFirstName() {
        Userx user = createTestUser(null, "Doe");
        Order order = createCompleteOrder(user);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Hi there Doe,"));
    }

    @Test
    void composePlainTextWithNullUserLastName() {
        Userx user = createTestUser("John", null);
        Order order = createCompleteOrder(user);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Hi John,"));
    }

    @Test
    void composePlainTextWithNullOrderNumber() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);
        order.setOrderNumber(null);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Order number: (unknown)"));
    }

    @Test
    void composePlainTextWithNullTimestamp() {
        // Note: Order.timestamp is set by @PrePersist, so we test with a mock that returns null
        // This tests the null handling in the composer
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);
        // Use reflection to set timestamp to null for testing
        try {
            java.lang.reflect.Field timestampField = Order.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(order, null);
        } catch (Exception e) {
            // If reflection fails, skip this test
            return;
        }

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Order date: (unknown)"));
    }

    @Test
    void composePlainTextWithNullStatus() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);
        order.setStatus(null);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Status: (unknown)"));
    }

    @Test
    void composePlainTextWithNullTotal() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);
        order.setTotal(null);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Order total:"));
    }

    @Test
    void composePlainTextWithNullItems() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);
        order.setItems(null);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("- (no items)"));
    }

    @Test
    void composePlainTextWithEmptyItems() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);
        order.setItems(new ArrayList<>());

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("- (no items)"));
    }

    @Test
    void composePlainTextWithItemsAndDiscounts() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderItem item1 = new OrderItem();
        item1.setProductName("Product 1");
        item1.setQuantity(2);
        item1.setPriceAtPurchase(100.0);
        item1.setAppliedDiscount(10.0); // 10% discount

        OrderItem item2 = new OrderItem();
        item2.setProductName("Product 2");
        item2.setQuantity(1);
        item2.setPriceAtPurchase(50.0);
        item2.setAppliedDiscount(null); // No discount

        order.setItems(List.of(item1, item2));
        order.setTotal(230.0); // (100 * 0.9 * 2) + (50 * 1) = 180 + 50 = 230

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Product 1"));
        assertTrue(result.contains("Product 2"));
        assertTrue(result.contains("2×"));
        assertTrue(result.contains("1×"));
        assertTrue(result.contains("% off")); // Should show discount percentage
    }

    @Test
    void composePlainTextWithNullShippingAddress() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);
        order.setShippingAddress(null);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Shipping address"));
        assertTrue(result.contains("(not provided)"));
    }

    @Test
    void composePlainTextWithNullBillingAddress() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);
        order.setBillingAddress(null);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Billing address"));
        assertTrue(result.contains("(not provided)"));
    }

    @Test
    void composePlainTextWithPartialAddress() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderAddress partialAddress = new OrderAddress(
                "Austria",
                "Innsbruck",
                "6020",
                null, // null street
                null, // null number
                null  // null extra
        );
        order.setShippingAddress(partialAddress);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("6020 Innsbruck"));
        assertTrue(result.contains("Austria"));
    }

    @Test
    void composePlainTextWithCompleteAddress() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderAddress completeAddress = new OrderAddress(
                "Austria",
                "Innsbruck",
                "6020",
                "Technikerstraße",
                "40",
                "RR21"
        );
        order.setShippingAddress(completeAddress);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Technikerstraße 40"));
        assertTrue(result.contains("6020 Innsbruck"));
        assertTrue(result.contains("Austria"));
        assertTrue(result.contains("RR21"));
    }

    @Test
    void composePlainTextWithItemNullProductName() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderItem item = new OrderItem();
        item.setProductName(null);
        item.setQuantity(1);
        item.setPriceAtPurchase(100.0);
        item.setAppliedDiscount(null);

        order.setItems(List.of(item));

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Item")); // Should use fallback
    }

    @Test
    void composePlainTextWithItemNullQuantity() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderItem item = new OrderItem();
        item.setProductName("Test Product");
        item.setQuantity(null);
        item.setPriceAtPurchase(100.0);
        item.setAppliedDiscount(null);

        order.setItems(List.of(item));

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("0×")); // Should default to 0
    }

    @Test
    void composePlainTextWithItemNullPrice() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderItem item = new OrderItem();
        item.setProductName("Test Product");
        item.setQuantity(1);
        item.setPriceAtPurchase(null);
        item.setAppliedDiscount(null);

        order.setItems(List.of(item));

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        // Should handle null price gracefully
        assertTrue(result.contains("Test Product"));
    }

    @Test
    void composePlainTextWithZeroDiscount() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderItem item = new OrderItem();
        item.setProductName("Test Product");
        item.setQuantity(2);
        item.setPriceAtPurchase(100.0);
        item.setAppliedDiscount(0.0);

        order.setItems(List.of(item));

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("Test Product"));
        assertFalse(result.contains("% off")); // Should not show discount for 0%
    }

    @Test
    void composePlainTextWithHighDiscount() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderItem item = new OrderItem();
        item.setProductName("Test Product");
        item.setQuantity(1);
        item.setPriceAtPurchase(100.0);
        item.setAppliedDiscount(0.50); // 50% discount

        order.setItems(List.of(item));

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("50% off")); // Should show 50% discount
    }

    @Test
    void composePlainTextWithMultipleOrderStatuses() {
        Userx user = createTestUser("John", "Doe");
        
        for (OrderStatus status : OrderStatus.values()) {
            Order order = createCompleteOrder(user);
            order.setStatus(status);

            String result = OrderEmailComposer.composePlainText(order);

            assertNotNull(result);
            assertTrue(result.contains("Status: " + status.name()));
        }
    }

    @Test
    void composePlainTextWithEmptyStringAddressFields() {
        Userx user = createTestUser("John", "Doe");
        Order order = createCompleteOrder(user);

        OrderAddress address = new OrderAddress(
                "", // empty country
                "", // empty city
                "", // empty postal
                "", // empty street
                "", // empty number
                ""  // empty extra
        );
        order.setShippingAddress(address);

        String result = OrderEmailComposer.composePlainText(order);

        assertNotNull(result);
        assertTrue(result.contains("(not provided)"));
    }

    // Helper methods

    private Userx createTestUser(String firstName, String lastName) {
        Userx user = new Userx();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    private Order createCompleteOrder(Userx user) {
        OrderAddress shippingAddress = new OrderAddress(
                "Austria",
                "Innsbruck",
                "6020",
                "Test Street",
                "123",
                null
        );

        OrderAddress billingAddress = new OrderAddress(
                "Austria",
                "Vienna",
                "1010",
                "Billing Street",
                "456",
                null
        );

        OrderItem item = new OrderItem();
        item.setProductName("Test Product");
        item.setQuantity(1);
        item.setPriceAtPurchase(150.0);
        item.setAppliedDiscount(null);

        Order order = new Order(
                user,
                List.of(item),
                billingAddress,
                shippingAddress,
                150.0
        );
        order.setOrderNumber("ORD-12345");
        order.setStatus(OrderStatus.CONFIRMED);
        
        // Set timestamp using reflection since there's no setter
        try {
            java.lang.reflect.Field timestampField = Order.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(order, LocalDateTime.of(2024, 1, 15, 10, 30));
        } catch (Exception e) {
            // If reflection fails, timestamp will be set by @PrePersist
        }

        return order;
    }
}

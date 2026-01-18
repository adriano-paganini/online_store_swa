package at.qe.skeleton.tests;

import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.model.ShippingMethod;
import at.qe.skeleton.services.OrderLifecycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderLifecycleServiceTest {

    private OrderLifecycleService lifecycleService;

    private void setTimestamp(Order order, LocalDateTime timestamp) {
        try {
            var field = Order.class.getDeclaredField("timestamp");
            field.setAccessible(true);
            field.set(order, timestamp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Order createOrder(
            OrderStatus status,
            ShippingMethod shippingMethod,
            LocalDateTime timestamp
    ) {
        Order order = new Order(
                null,
                List.of(),
                null,
                null,
                shippingMethod,
                100.0
        );
        order.setStatus(status);
        setTimestamp(order, timestamp);
        return order;
    }

    @BeforeEach
    void setUp() {
        lifecycleService = new OrderLifecycleService();
    }

    @Test
    void paidOrderBefore12HoursStaysPaid() {
        Order order = createOrder(
                OrderStatus.PAID,
                ShippingMethod.FAIRY_DUST_DISPATCH,
                LocalDateTime.now().minusHours(5));

        OrderStatus result =
                lifecycleService.resolveStatus(order, LocalDateTime.now());

        assertEquals(OrderStatus.PAID, result);
    }

    @Test
    void paidOrderAfter12HoursBecomesShipping() {
        Order order = createOrder(
                OrderStatus.PAID,
                ShippingMethod.FAIRY_DUST_DISPATCH,
                LocalDateTime.now().minusHours(13));

        OrderStatus result =
                lifecycleService.resolveStatus(order, LocalDateTime.now());

        assertEquals(OrderStatus.SHIPPING, result);
    }

    @Test
    void shippingOrderBeforeShippingDurationStaysShipping() {
        Order order = createOrder(
                OrderStatus.SHIPPING,
                ShippingMethod.CARRIER_PIGEON,
                LocalDateTime.now().minusHours(20));

        OrderStatus result =
                lifecycleService.resolveStatus(order, LocalDateTime.now());

        assertEquals(OrderStatus.SHIPPING, result);
    }

    @Test
    void shippingOrderAfterShippingDurationBecomesDelivered() {
        Order order = createOrder(
                OrderStatus.SHIPPING,
                ShippingMethod.FAIRY_DUST_DISPATCH,
                LocalDateTime.now().minusHours(40));

        OrderStatus result =
                lifecycleService.resolveStatus(order, LocalDateTime.now());

        assertEquals(OrderStatus.DELIVERED, result);
    }


    @Test
    void applyResolvedStatus_updatesOrderAndReturnsTrueWhenStatusChanges() {
        Order order = createOrder(
                OrderStatus.PAID,
                ShippingMethod.FAIRY_DUST_DISPATCH,
                LocalDateTime.now().minusHours(13));

        boolean changed =
                lifecycleService.applyResolvedStatus(order, LocalDateTime.now());

        assertTrue(changed);
        assertEquals(OrderStatus.SHIPPING, order.getStatus());
    }

    @Test
    void applyResolvedStatus_doesNothingAndReturnsFalseWhenNoChange() {
        Order order = createOrder(
                OrderStatus.PAID,
                ShippingMethod.FAIRY_DUST_DISPATCH,
                LocalDateTime.now().minusHours(3));

        boolean changed =
                lifecycleService.applyResolvedStatus(order, LocalDateTime.now());

        assertFalse(changed);
        assertEquals(OrderStatus.PAID, order.getStatus());
    }
}

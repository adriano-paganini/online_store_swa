package at.qe.skeleton.tests;

import at.qe.skeleton.events.*;
import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;
import at.qe.skeleton.model.Userx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductEventTest {

    private Product product;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setName("Test Smartphone");
    }

    @Test
    void testProductRestockEvent() {
        int oldVal = 0;
        int newVal = 5;
        ProductRestockEvent event = new ProductRestockEvent(product, oldVal, newVal);

        String subject = event.getPayloadSubjectLine();
        String timestamp = event.getTimestamp().format(formatter);

        assertEquals(SubscriptionType.RESTOCK, event.getSubscriptionType());
        assertEquals(newVal, event.getNewValue());
        assertTrue(subject.contains(timestamp));
        assertTrue(subject.contains("Test Smartphone"));
        assertTrue(subject.contains("5 pieces are available"));
    }

    @Test
    void testProductPriceUpdateEvent() {
        double oldPrice = 100.00;
        double newPrice = 85.50;
        ProductPriceUpdateEvent event = new ProductPriceUpdateEvent(product, oldPrice, newPrice);

        String subject = event.getPayloadSubjectLine();

        assertEquals(SubscriptionType.PRICEUPDATE, event.getSubscriptionType());
        assertTrue(subject.contains("100.00"));
        assertTrue(subject.contains("85.50"));
    }

    @Test
    void testProductNameUpdateEvent() {
        String oldName = "Old Name";
        String newName = "New Shiny Name";
        ProductNameUpdateEvent event = new ProductNameUpdateEvent(product, oldName, newName);

        String subject = event.getPayloadSubjectLine();

        assertEquals(SubscriptionType.NAMEUPDATE, event.getSubscriptionType());
        assertTrue(subject.contains("The Name of \"Old Name\" has been updated to: New Shiny Name"));
    }

    @Test
    void testProductDescriptionUpdateEvent() {
        ProductDescriptionUpdateEvent event = new ProductDescriptionUpdateEvent(product, "old", "new");

        String subject = event.getPayloadSubjectLine();

        assertEquals(SubscriptionType.DESCRIPTIONUPDATE, event.getSubscriptionType());
        assertTrue(subject.contains("The description of \"Test Smartphone\" has changed"));
    }

    @Test
    void testProductRestockEventGrammarSingular() {
        ProductRestockEvent event = new ProductRestockEvent(product, 0, 1);
        assertTrue(event.getPayloadSubjectLine().contains("1 piece is available"));
    }
    @Test
    void testProductDiscountUpdateEventSubjectLine() {
        Product product = new Product();
        product.setName("Summer Shoes");
        Double oldDiscount = 5.0;
        Double newDiscount = 15.5;
        ProductDiscountUpdateEvent event = new ProductDiscountUpdateEvent(product, oldDiscount, newDiscount);

        String subject = event.getPayloadSubjectLine();
        String expectedDate = event.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        assertTrue(subject.startsWith(expectedDate));
        assertTrue(subject.contains("Summer Shoes"));

        assertTrue(subject.contains("5.00"));
        assertTrue(subject.contains("15.50"));
        assertEquals("DISCOUNTUPDATE", event.getSubscriptionType().name());
    }

    @Test
    void testOrderCompletionEventWithMock() {
        Order mockOrder = mock(Order.class);
        Userx mockUser = mock(Userx.class);

        when(mockOrder.getUser()).thenReturn(mockUser);
        when(mockUser.getFirstName()).thenReturn("Jane");
        when(mockUser.getLastName()).thenReturn("Doe");
        when(mockOrder.getOrderNumber()).thenReturn("ORD-MOCK-123");
        when(mockOrder.getTotal()).thenReturn(250.0);
        when(mockOrder.getStatus()).thenReturn(at.qe.skeleton.model.OrderStatus.CONFIRMED);

        OrderCompletionEvent event = new OrderCompletionEvent(mockOrder);

        String result = event.getPayloadSubjectLine();

        assertNotNull(result);
        assertTrue(result.contains("Hi Jane Doe"));
        assertTrue(result.contains("ORD-MOCK-123"));
        assertTrue(result.contains("250.00") || result.contains("250")); // Depends on your Locale settings
    }
}
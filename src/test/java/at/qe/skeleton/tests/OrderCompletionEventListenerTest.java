package at.qe.skeleton.tests;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.events.OrderCompletionEvent;
import at.qe.skeleton.listeners.OrderCompletionEventListener;
import at.qe.skeleton.model.*;
import at.qe.skeleton.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderCompletionEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private OrderCompletionEventListener listener;

    private Order testOrder;
    private Userx testUser;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

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
        item.setPriceAtPurchase(100.0);
        item.setAppliedDiscount(null);

        testOrder = new Order(
                testUser,
                List.of(item),
                billingAddress,
                shippingAddress,
                100.0
        );
        testOrder.setOrderNumber("ORD-12345");
        testOrder.setStatus(OrderStatus.CONFIRMED);
        
        // Set timestamp using reflection
        try {
            java.lang.reflect.Field timestampField = Order.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(testOrder, LocalDateTime.now());
        } catch (Exception e) {
            // Ignore if reflection fails
        }
    }

    @Test
    void handleOrderCompleteEventCreatesNotification() {
        OrderCompletionEvent event = new OrderCompletionEvent(testOrder);
        Notification mockNotification = new Notification();
        mockNotification.setId(1L);
        mockNotification.setUserId(testUser.getId());
        mockNotification.setChannel(NotificationType.EMAIL);

        when(notificationService.createNotification(
                eq(testUser.getId()),
                eq(NotificationType.EMAIL),
                eq(event)
        )).thenReturn(mockNotification);

        listener.handleOrderCompleteEvent(event);

        verify(notificationService, times(1)).createNotification(
                eq(testUser.getId()),
                eq(NotificationType.EMAIL),
                eq(event)
        );
    }

    @Test
    void handleOrderCompleteEventPublishesEmailNotificationEvent() {
        OrderCompletionEvent event = new OrderCompletionEvent(testOrder);
        Notification mockNotification = new Notification();
        mockNotification.setId(1L);
        mockNotification.setUserId(testUser.getId());
        mockNotification.setChannel(NotificationType.EMAIL);

        when(notificationService.createNotification(
                anyLong(),
                any(NotificationType.class),
                any(OrderCompletionEvent.class)
        )).thenReturn(mockNotification);

        listener.handleOrderCompleteEvent(event);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<EmailNotificationEvent<?>> eventCaptor = ArgumentCaptor.forClass(EmailNotificationEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        EmailNotificationEvent<?> publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        assertEquals(mockNotification.getId(), publishedEvent.getNotificationId());
    }

    @Test
    void handleOrderCompleteEventWithNullOrderUser() {
        testOrder.setUser(null);
        OrderCompletionEvent event = new OrderCompletionEvent(testOrder);

        assertThrows(NullPointerException.class, () -> {
            listener.handleOrderCompleteEvent(event);
        });
    }

    @Test
    void handleOrderCompleteEventWithNullOrder() {
        OrderCompletionEvent event = new OrderCompletionEvent(null);

        assertThrows(Exception.class, () -> {
            listener.handleOrderCompleteEvent(event);
        });
    }

    @Test
    void handleOrderCompleteEventVerifiesNotificationServiceCall() {
        OrderCompletionEvent event = new OrderCompletionEvent(testOrder);
        Notification mockNotification = new Notification();
        mockNotification.setId(1L);

        when(notificationService.createNotification(
                anyLong(),
                any(NotificationType.class),
                any(OrderCompletionEvent.class)
        )).thenReturn(mockNotification);

        listener.handleOrderCompleteEvent(event);

        verify(notificationService, times(1)).createNotification(
                eq(1L),
                eq(NotificationType.EMAIL),
                eq(event)
        );
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void handleOrderCompleteEventVerifiesEventPublisherCall() {
        OrderCompletionEvent event = new OrderCompletionEvent(testOrder);
        Notification mockNotification = new Notification();
        mockNotification.setId(1L);

        when(notificationService.createNotification(
                anyLong(),
                any(NotificationType.class),
                any(OrderCompletionEvent.class)
        )).thenReturn(mockNotification);

        listener.handleOrderCompleteEvent(event);

        verify(applicationEventPublisher, times(1)).publishEvent(any(EmailNotificationEvent.class));
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    void handleOrderCompleteEventWithDifferentOrderStatuses() {
        for (OrderStatus status : OrderStatus.values()) {
            testOrder.setStatus(status);
            OrderCompletionEvent event = new OrderCompletionEvent(testOrder);
            Notification mockNotification = new Notification();
            mockNotification.setId(1L);

            when(notificationService.createNotification(
                    anyLong(),
                    any(NotificationType.class),
                    any(OrderCompletionEvent.class)
            )).thenReturn(mockNotification);

            listener.handleOrderCompleteEvent(event);

            verify(notificationService, atLeastOnce()).createNotification(
                    anyLong(),
                    eq(NotificationType.EMAIL),
                    any(OrderCompletionEvent.class)
            );

            reset(notificationService, applicationEventPublisher);
        }
    }
}

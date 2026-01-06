package at.qe.skeleton.tests;

import at.qe.skeleton.events.ProductEvent;
import at.qe.skeleton.listeners.SubscriptionNotificationListener;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.SubscriptionRepository;
import at.qe.skeleton.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SubscriptionNotificationListenerTest {

    private SubscriptionRepository subscriptionRepository;
    private NotificationService notificationService;
    private ApplicationEventPublisher applicationEventPublisher;

    private SubscriptionNotificationListener listener;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        notificationService = mock(NotificationService.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);

        listener = new SubscriptionNotificationListener(
                subscriptionRepository,
                notificationService,
                applicationEventPublisher
        );
    }

    @Test
    void handleProductUpdateShouldCreateNotificationsAndPublishEventsForAllMatchingSubscriptionsAndChannels() {
        ProductEvent<?> event = mock(ProductEvent.class);
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(10L);
        when(event.getProduct()).thenReturn(product);

        SubscriptionType subscriptionType = SubscriptionType.RESTOCK;
        when(event.getSubscriptionType()).thenReturn(subscriptionType);

        Subscription s1 = mock(Subscription.class);
        Subscription s2 = mock(Subscription.class);

        Userx u1 = mock(Userx.class);
        Userx u2 = mock(Userx.class);
        when(u1.getId()).thenReturn(111L);
        when(u2.getId()).thenReturn(222L);

        when(s1.getUser()).thenReturn(u1);
        when(s2.getUser()).thenReturn(u2);

        when(s1.getChannels()).thenReturn(Set.of(NotificationType.EMAIL, NotificationType.SMS));
        when(s2.getChannels()).thenReturn(Set.of(NotificationType.EMAIL, NotificationType.SMS));

        when(subscriptionRepository.findByProductAndType(10L, subscriptionType))
                .thenReturn(List.of(s1, s2));

        Notification n1Email = mock(Notification.class);
        Notification n1Sms = mock(Notification.class);
        Notification n2Email = mock(Notification.class);
        Notification n2Sms = mock(Notification.class);


        when(notificationService.createNotification(eq(111L), eq(NotificationType.EMAIL), same(event))).thenReturn(n1Email);
        when(notificationService.createNotification(eq(111L), eq(NotificationType.SMS), same(event))).thenReturn(n1Sms);
        when(notificationService.createNotification(eq(222L), eq(NotificationType.EMAIL), same(event))).thenReturn(n2Email);
        when(notificationService.createNotification(eq(222L), eq(NotificationType.SMS), same(event))).thenReturn(n2Sms);

        listener.handleProductUpdate(event);

        verify(subscriptionRepository, times(1)).findByProductAndType(10L, subscriptionType);

        verify(notificationService, times(1)).createNotification(111L, NotificationType.EMAIL, event);
        verify(notificationService, times(1)).createNotification(111L, NotificationType.SMS, event);
        verify(notificationService, times(1)).createNotification(222L, NotificationType.EMAIL, event);
        verify(notificationService, times(1)).createNotification(222L, NotificationType.SMS, event);

        ArgumentCaptor<Object> publishedEvents = ArgumentCaptor.forClass(Object.class);
        verify(applicationEventPublisher, times(4)).publishEvent(publishedEvents.capture());

        List<Object> allPublished = publishedEvents.getAllValues();
        assertThat(allPublished).hasSize(4);
        assertThat(allPublished).allSatisfy(e -> assertThat(e).isNotNull());

        verifyNoMoreInteractions(subscriptionRepository, notificationService, applicationEventPublisher);
    }

    @Test
    void handleProductUpdateShouldDoNothingWhenNoMatchingSubscriptions() {
        ProductEvent<?> event = mock(ProductEvent.class);
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(10L);
        when(event.getProduct()).thenReturn(product);

        SubscriptionType subscriptionType = SubscriptionType.DESCRIPTIONUPDATE;
        when(event.getSubscriptionType()).thenReturn(subscriptionType);

        when(subscriptionRepository.findByProductAndType(10L, subscriptionType))
                .thenReturn(List.of());

        listener.handleProductUpdate(event);

        verify(subscriptionRepository, times(1)).findByProductAndType(10L, subscriptionType);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(applicationEventPublisher);
        verifyNoMoreInteractions(subscriptionRepository);
    }
}

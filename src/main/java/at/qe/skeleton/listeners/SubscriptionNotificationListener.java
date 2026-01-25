package at.qe.skeleton.listeners;

import at.qe.skeleton.events.Payload;
import at.qe.skeleton.events.ProductEvent;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.repositories.SubscriptionRepository;
import at.qe.skeleton.services.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * Event listener responsible for processing {@link ProductEvent}s and notifying subscribers.
 * <p>
 * This component identifies all users subscribed to a specific product and event type,
 * creates persistent notification records for them, and dispatches delivery events
 * through the user's preferred notification channels (e.g., Email, SMS).
 */
@Component
public class SubscriptionNotificationListener {

    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SubscriptionNotificationListener(SubscriptionRepository subscriptionRepository, NotificationService notificationService, ApplicationEventPublisher applicationEventPublisher) {
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // Listens for product-related changes and triggers the notification fan-out process
    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductUpdate(ProductEvent<?> event) {
        // Retrieve all unique subscriptions that match the product and the specific change type (e.g., Price Drop)
        List<Subscription> matchingSubscriptions = subscriptionRepository.findByProductAndType(
                        event.getProduct().getId(),
                        event.getSubscriptionType())
                .stream()
                .distinct()
                .toList();

        // Iterate through each matching subscriber to generate individualized notifications
        for (Subscription s : matchingSubscriptions) {
            // Contextualize the event with the specific subscription data
            event.setPayloadInfo(s);

            // Create a notification for every channel (Email, SMS, etc.) the user has opted into
            for (NotificationType channel : s.getChannels()) {
                // Persist the notification in the database for the user's history
                Notification notification = notificationService.createNotification(s.getUser().getId(), channel, event);

                // Wrap the event in a payload and publish a channel-specific event (Strategy Pattern)
                Payload<ProductEvent<?>> payload = new Payload<>(event);
                applicationEventPublisher.publishEvent(channel.createEvent(notification, payload));
            }
        }
    }
}
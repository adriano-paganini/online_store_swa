package at.qe.skeleton.listeners;

import at.qe.skeleton.events.ProductEvent;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.repositories.SubscriptionRepository;
import at.qe.skeleton.services.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Async
    @EventListener
    @Transactional
    public void handleProductUpdate(ProductEvent<?> event) {
        List<Subscription> matchingSubscriptions = subscriptionRepository.findByProductAndType(
                event.getProduct().getId(),
                event.getSubscriptionType());

        for (Subscription s : matchingSubscriptions) {
            for (NotificationType channel : s.getChannels()) {
                Notification notification = notificationService.createNotification(s.getUser().getId(), channel, event);
                applicationEventPublisher.publishEvent(channel.createEvent(notification));
            }

        }
    }
}

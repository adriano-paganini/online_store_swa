package at.qe.skeleton.services;

import at.qe.skeleton.events.ProductEvent;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionNotificationListener {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionNotificationListener(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Async
    @EventListener
    @Transactional
    public void handleProductUpdate(ProductEvent<?> event) {
        List<Subscription> matchingSubscriptions = subscriptionRepository.findByProductAndType(
                event.getProduct().getId(),
                event.getSubscriptionType());

        for (Subscription s : matchingSubscriptions) {
            Userx user = s.getUser();
            //TODO: IMPLEMENT NOTIFICATIONS FOR USER

        }
    }
}

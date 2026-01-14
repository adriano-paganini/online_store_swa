package at.qe.skeleton.listeners;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Order;
import at.qe.skeleton.events.OrderCompletionEvent;
import at.qe.skeleton.services.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCompletionEventListener {

    private final NotificationService notificationService;
    private final ApplicationEventPublisher applicationEventPublisher;


    public OrderCompletionEventListener(NotificationService notificationService, ApplicationEventPublisher applicationEventPublisher) {
        this.notificationService = notificationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async
    @Transactional
    @TransactionalEventListener(phase= TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleteEvent(OrderCompletionEvent event){
        Order order = event.getPayloadInfo();
        Notification notification = notificationService.createNotification(order.getUser().getId(), NotificationType.EMAIL, event);
        applicationEventPublisher.publishEvent(new EmailNotificationEvent<>(notification,event));
    }
}

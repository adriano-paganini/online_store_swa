package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.Subscription;

public class NotificationEvent {
    private final Long notificationId;
    private final Subscription subscription;

    public NotificationEvent(Notification notification, Subscription subscription) {
        this.notificationId = notification.getId();
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public Long getNotificationId() {
        return notificationId;
    }
}
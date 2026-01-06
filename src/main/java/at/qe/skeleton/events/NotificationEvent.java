package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.Subscription;

public class NotificationEvent {
    private final Notification notification;
    private final Subscription subscription;

    public NotificationEvent(Notification notification, Subscription subscription) {
        this.notification = notification;
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public Notification getNotification() {
        return notification;
    }
}
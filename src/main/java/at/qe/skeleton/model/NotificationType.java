package at.qe.skeleton.model;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.events.NotificationEvent;
import at.qe.skeleton.events.SmsNotificationEvent;

import java.util.function.BiFunction;

public enum NotificationType {
    EMAIL(EmailNotificationEvent::new),
    SMS(SmsNotificationEvent::new);

    private final BiFunction<Notification, Subscription, NotificationEvent> eventConstructor;

    NotificationType(BiFunction<Notification, Subscription, NotificationEvent> eventConstructor) {
        this.eventConstructor = eventConstructor;
    }

    public NotificationEvent createEvent(Notification n, Subscription s) {
        return eventConstructor.apply(n, s);
    }
}
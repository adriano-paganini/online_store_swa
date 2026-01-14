package at.qe.skeleton.model;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.events.NotificationEvent;
import at.qe.skeleton.events.Payload;
import at.qe.skeleton.events.SmsNotificationEvent;

import java.util.function.BiFunction;

public enum NotificationType {
    EMAIL(EmailNotificationEvent::new),
    SMS(SmsNotificationEvent::new);

    private final BiFunction<Notification, Payload<?>, NotificationEvent<?>> eventConstructor;

    NotificationType(BiFunction<Notification, Payload<?>, NotificationEvent<?>> eventConstructor) {
        this.eventConstructor = eventConstructor;
    }

    public NotificationEvent<?> createEvent(Notification notification, Payload<?> payload) {
        return eventConstructor.apply(notification, payload);
    }
}
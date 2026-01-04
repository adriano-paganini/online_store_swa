package at.qe.skeleton.model;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.events.NotificationEvent;
import at.qe.skeleton.events.SmsNotificationEvent;

import java.util.function.Function;

public enum NotificationType {
    EMAIL(EmailNotificationEvent::new),
    SMS(SmsNotificationEvent::new);

    private final Function<Notification, NotificationEvent> eventConstructor;

    NotificationType(Function<Notification, NotificationEvent> eventConstructor) {
        this.eventConstructor = eventConstructor;
    }

    public NotificationEvent createEvent(Notification n) {
        return eventConstructor.apply(n);
    }
}

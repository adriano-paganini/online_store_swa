package at.qe.skeleton.model;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.events.NotificationEvent;
import at.qe.skeleton.events.Payload;
import at.qe.skeleton.events.SmsNotificationEvent;

import java.util.function.BiFunction;

/**
 * Enum representing the available notification delivery channels.
 * <p>
 * This implementation utilizes a <b>Strategy Pattern</b> to encapsulate the logic
 * for instantiating the correct {@link NotificationEvent} subclass based on the selected type.
 */
public enum NotificationType {
    // Each constant acts as a concrete strategy, passing its specific constructor reference
    EMAIL(EmailNotificationEvent::new),
    SMS(SmsNotificationEvent::new);

    // Functional interface used to store the constructor reference for the specific event type
    private final BiFunction<Notification, Payload<?>, NotificationEvent<?>> eventConstructor;

    NotificationType(BiFunction<Notification, Payload<?>, NotificationEvent<?>> eventConstructor) {
        this.eventConstructor = eventConstructor;
    }

    public NotificationEvent<?> createEvent(Notification notification, Payload<?> payload) {
        // Executes the stored strategy to produce the relevant class instance
        return eventConstructor.apply(notification, payload);
    }
}
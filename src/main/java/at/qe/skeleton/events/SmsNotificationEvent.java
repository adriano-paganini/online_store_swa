package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;

/**
 * Specialized event for triggering SMS notifications.
 * * Hierarchy: Extends NotificationEvent.
 * Similar to EmailNotificationEvent, this acts as a specific type marker for
 * the SMS dispatch service to listen for.
 * @param <T> The type of payload carried by this SMS event.
 */
public class SmsNotificationEvent<T extends PayloadInterface> extends NotificationEvent<T> {

    public SmsNotificationEvent(Notification notification, Payload<T> payload) {
        super(notification, payload);
    }
}

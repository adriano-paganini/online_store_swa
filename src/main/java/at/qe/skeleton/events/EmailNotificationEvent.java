package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;

/**
 * Specialized event for triggering email notifications.
 * * Hierarchy: Extends NotificationEvent. It acts as a specific type marker so the
 * EmailService knows which events to handle.
 */
public class EmailNotificationEvent<T extends PayloadInterface> extends NotificationEvent<T> {

    public EmailNotificationEvent(Notification notification, Payload<T> payload) {
        super(notification, payload);
    }
}

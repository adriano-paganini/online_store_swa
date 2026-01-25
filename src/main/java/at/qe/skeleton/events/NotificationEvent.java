package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;


/**
 * The base class for system events that trigger a notification.
 * * Hierarchy: Independent of the Payload hierarchy, but contains a Payload object.
 * @param <T> The type of payload carried by this event.
 */
public class NotificationEvent<T extends PayloadInterface> {
    private final Long notificationId;
    private final Payload<T> payload;

    public NotificationEvent(Notification notification, Payload<T> payload) {
        this.notificationId = notification.getId();
        this.payload = payload;
    }

    public Payload<? extends PayloadInterface> getPayload() {
        return payload;
    }

    public Long getNotificationId() {
        return notificationId;
    }
}
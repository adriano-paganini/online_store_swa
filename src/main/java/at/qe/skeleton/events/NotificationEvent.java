package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;


public class NotificationEvent<T extends PayloadInterface> {
    private final Long notificationId;
    private final Payload<T> payload;

    public NotificationEvent(Notification notification,Payload<T> payload) {
        this.notificationId = notification.getId();
        this.payload = payload;
    }

    public Payload<?> getPayload() {
        return payload;
    }

    public Long getNotificationId() {
        return notificationId;
    }
}
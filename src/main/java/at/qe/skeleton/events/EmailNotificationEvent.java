package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;

public class EmailNotificationEvent<T extends PayloadInterface> extends NotificationEvent<T> {

    public EmailNotificationEvent(Notification notification, Payload<T> payload) {
        super(notification, payload);
    }
}

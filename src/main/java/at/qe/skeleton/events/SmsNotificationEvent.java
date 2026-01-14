package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;

public class SmsNotificationEvent<T extends PayloadInterface> extends NotificationEvent<T> {

    public SmsNotificationEvent(Notification notification, Payload<T> payload) {
        super(notification, payload);
    }

}

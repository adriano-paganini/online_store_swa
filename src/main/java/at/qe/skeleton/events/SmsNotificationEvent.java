package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;

public class SmsNotificationEvent extends NotificationEvent {

    public SmsNotificationEvent(Notification notification) {
        super(notification);
    }

}

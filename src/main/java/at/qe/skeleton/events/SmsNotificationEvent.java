package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.Subscription;

public class SmsNotificationEvent extends NotificationEvent {

    public SmsNotificationEvent(Notification notification, Subscription subscription) {
        super(notification, subscription);
    }

}

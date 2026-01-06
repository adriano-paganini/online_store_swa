package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.Subscription;

public class EmailNotificationEvent extends NotificationEvent {

    public EmailNotificationEvent(Notification notification, Subscription subscription) {
        super(notification, subscription);
    }
}

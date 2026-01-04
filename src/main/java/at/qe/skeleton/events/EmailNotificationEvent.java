package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;

public class EmailNotificationEvent extends NotificationEvent {

    public EmailNotificationEvent(Notification notification) {
        super(notification);
    }
}

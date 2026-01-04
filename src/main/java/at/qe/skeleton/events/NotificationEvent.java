package at.qe.skeleton.events;

import at.qe.skeleton.model.Notification;

public class NotificationEvent {
    private final Notification notification;

    public NotificationEvent(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
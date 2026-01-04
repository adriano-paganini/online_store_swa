package at.qe.skeleton.services;

import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

    private final NotificationService notificationService;

    public SmsNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void sendSms(Notification n) {
        //This is a Stub. The actual functionality is not needed, as this is just a proof of concept.
        //90% success rate
        boolean sendingSuccess = Math.random() < 0.9;
        if (sendingSuccess) {
            notificationService.updateNotificationStatus(NotificationStatus.SENT, n);
        } else {
            notificationService.updateNotificationStatus(NotificationStatus.FAILED, n);
        }
    }
}

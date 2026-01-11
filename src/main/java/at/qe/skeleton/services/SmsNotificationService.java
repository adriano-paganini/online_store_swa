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

    public void sendSms(Long notificationId) {
        //This is a Stub. The actual functionality is not needed, as this is just a proof of concept.
        //90% success rate
        Notification notification = notificationService.getNotificationById(notificationId);
        boolean sendingSuccess = Math.random() < 0.9;
        if (sendingSuccess) {
            notificationService.updateNotificationStatus(NotificationStatus.SENT, notification);
        } else {
            notificationService.updateNotificationStatus(NotificationStatus.FAILED, notification);
        }
    }
}

package at.qe.skeleton.services;

import at.qe.skeleton.events.Payload;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import org.springframework.stereotype.Service;

/**
 * Service responsible for the simulated delivery of SMS notifications.
 * <p>
 * This class serves as a proof-of-concept (stub) implementation. It demonstrates
 * how an SMS delivery service would interact with the {@link NotificationService}
 * to retrieve message data and update delivery statuses based on transmission results.
 */
@Service
public class SmsNotificationService {

    private final NotificationService notificationService;

    public SmsNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Sends an SMS message
     *
     * @param notificationId the id of the notification to send
     * @param payload the message payload to send
     */
    public void sendSms(Long notificationId, Payload<?> payload) {
        // Retrieve the notification entity to access recipient details
        Notification notification = notificationService.getNotificationById(notificationId);

        // Log the simulated output to the console
        System.out.println(payload.getPayloadInfo().getPayloadSubjectLine() + " for User: " + notification.getUserId());

        // Simulate a real-world network failure scenario with a 90% success probability
        boolean sendingSuccess = Math.random() < 0.9;

        if (sendingSuccess) {
            // Update the audit trail to SENT if the simulation succeeds
            notificationService.updateNotificationStatus(NotificationStatus.SENT, notification);
        } else {
            // Update the audit trail to FAILED if the simulation fails
            notificationService.updateNotificationStatus(NotificationStatus.FAILED, notification);
        }
    }
}
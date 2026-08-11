package at.qe.skeleton.services;

import at.qe.skeleton.events.NotificationEvent;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.Userx;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Service responsible for the physical delivery of email notifications.
 * <p>
 * This service handles the integration with the SMTP server, message construction,
 * and updating the notification audit trail based on the success or failure of the delivery.
 */
@Service
public class EmailNotificationService {

    private final NotificationService notificationService;
    private final UserxService userxService;
    private final String smtpHost;
    private final String smtpPort;
    private final String smtpUsername;
    private final String smtpPassword;
    private final String smtpStarttlsEnable;

    public EmailNotificationService(
            NotificationService notificationService,
            UserxService userxService,
            @Value("${app.mail.smtp.host:}") String smtpHost,
            @Value("${app.mail.smtp.port:587}") String smtpPort,
            @Value("${app.mail.smtp.username:}") String smtpUsername,
            @Value("${app.mail.smtp.password:}") String smtpPassword,
            @Value("${app.mail.smtp.starttls.enable:true}") String smtpStarttlsEnable
    ) {
        this.notificationService = notificationService;
        this.userxService = userxService;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
        this.smtpStarttlsEnable = smtpStarttlsEnable;
    }

    /**
     * Sends an email based on a notification event.
     * <p>
     * Resolves the recipient user, constructs the email message,
     * performs SMTP delivery, and updates the notification status
     * to {@link NotificationStatus#SENT} or {@link NotificationStatus#FAILED}
     * depending on the outcome.
     *
     * @param event the notification event triggering the email delivery
     */
    @Transactional
    public void sendEmail(NotificationEvent<?> event) {
        // Reload notification from the database to ensure data consistency in asynchronous contexts
        Notification notification = notificationService.getNotificationById(event.getNotificationId());

        try {
            // Retrieve the recipient user information
            Userx user = userxService.getUserById(notification.getUserId());

            // Validate user and email presence; mark as failed if destination is unreachable
            if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
                notificationService.updateNotificationStatus(NotificationStatus.FAILED, notification);
                return;
            }

            if (smtpHost == null || smtpHost.isBlank()
                    || smtpUsername == null || smtpUsername.isBlank()
                    || smtpPassword == null || smtpPassword.isBlank()) {
                notificationService.updateNotificationStatus(NotificationStatus.FAILED, notification);
                return;
            }

            Properties properties = new Properties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.host", smtpHost);
            properties.put("mail.smtp.port", smtpPort);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.user", smtpUsername);
            properties.put("mail.smtp.password", smtpPassword);
            properties.put("mail.smtp.starttls.enable", smtpStarttlsEnable);

            // Create a mail session with the specified authenticator
            Session mailSession = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            properties.getProperty("mail.smtp.user"),
                            properties.getProperty("mail.smtp.password")
                    );
                }
            });

            // Construct the MimeMessage
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(smtpUsername));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

            // Set the subject and body text from the notification and event payload
            message.setSubject(notification.getMessage());
            message.setText(event.getPayload().getPayloadSubjectLine());

            // Execute the physical transport
            Transport.send(message);

            // Mark notification as SENT upon successful transmission
            notificationService.updateNotificationStatus(NotificationStatus.SENT, notification);

        } catch (MessagingException e) {
            // Mark notification as FAILED if a transport or protocol error occurs
            notificationService.updateNotificationStatus(NotificationStatus.FAILED, notification);
        }
    }
}

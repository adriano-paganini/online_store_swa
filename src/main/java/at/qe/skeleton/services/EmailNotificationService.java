package at.qe.skeleton.services;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.Userx;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailNotificationService {

    private final NotificationService notificationService;

    public EmailNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void sendEmail(EmailNotificationEvent event) {
        Subscription subscription = event.getSubscription();
        Notification notification = event.getNotification();
        Userx user = subscription.getUser();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            notificationService.updateNotificationStatus(NotificationStatus.FAILED,notification);
            return;
        }
        String sender = "software.architektur@gmx.at";
        String password = "software.architektur@gmx.at";

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "mail.gmx.net");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.user", sender);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.starttls.enable", "true");
        try {
            Session mailSession = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            properties.getProperty("mail.smtp.user"),
                            properties.getProperty("mail.smtp.password")
                    );
                }
            });

            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(sender));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

            message.setSubject(notification.getChannel().toString() + " Subscription Update for " + subscription.getProduct().getName());
            message.setText(notification.getMessage());

            Transport.send(message);

            notificationService.updateNotificationStatus(NotificationStatus.SENT, notification);

        } catch (MessagingException e) {
            notificationService.updateNotificationStatus(NotificationStatus.FAILED, notification);
            System.err.println("Failed to send email to " + user.getEmail() + ": " + e.getMessage());
        }
    }
}
package at.qe.skeleton.listeners;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.services.EmailNotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationEventListener {

    private final EmailNotificationService emailNotificationService;

    public EmailNotificationEventListener(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @EventListener
    public void handleEmailNotificationEvent(EmailNotificationEvent event) {
        emailNotificationService.sendEmail(event.getNotification());
    }
}

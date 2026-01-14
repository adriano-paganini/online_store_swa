package at.qe.skeleton.listeners;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.services.EmailNotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
public class EmailNotificationEventListener {

    private final EmailNotificationService emailNotificationService;

    public EmailNotificationEventListener(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailNotificationEvent(EmailNotificationEvent<?> event) {
        System.out.println("Reached EmailNotificationEventListener");
        emailNotificationService.sendEmail(event);
    }
}

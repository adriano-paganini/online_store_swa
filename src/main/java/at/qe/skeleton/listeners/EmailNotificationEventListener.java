package at.qe.skeleton.listeners;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.services.EmailNotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener for {@link EmailNotificationEvent}s.
 * <p>
 * This component listens for email-specific notification events and triggers the delivery
 * process via the {@link EmailNotificationService}. It is designed to run asynchronously
 * only after the originating transaction has successfully committed.
 */
@Component
public class EmailNotificationEventListener {

    private final EmailNotificationService emailNotificationService;

    public EmailNotificationEventListener(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    // Triggered asynchronously after the transaction that published the event has successfully committed
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailNotificationEvent(EmailNotificationEvent<?> event) {
        // Delegate the physical email delivery to the specialized service
        emailNotificationService.sendEmail(event);
    }
}
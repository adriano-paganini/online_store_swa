package at.qe.skeleton.listeners;

import at.qe.skeleton.events.SmsNotificationEvent;
import at.qe.skeleton.services.SmsNotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener for {@link SmsNotificationEvent}s.
 * <p>
 * This component handles SMS-specific notification events by delegating the delivery
 * logic to the {@link SmsNotificationService}. Execution occurs asynchronously only
 * after the successful completion of the transaction that triggered the event.
 */
@Component
public class SmsNotificationEventListener {

    private final SmsNotificationService smsNotificationService;

    public SmsNotificationEventListener(SmsNotificationService smsNotificationService) {
        this.smsNotificationService = smsNotificationService;
    }

    // Listens for SMS events and triggers simulated delivery after the transaction commits
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSmsNotificationEvent(SmsNotificationEvent<?> event) {
        // Extracts notification details from the event and initiates the SMS sending process
        smsNotificationService.sendSms(event.getNotificationId(), event.getPayload());
    }
}
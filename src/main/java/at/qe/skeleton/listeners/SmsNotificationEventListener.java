package at.qe.skeleton.listeners;

import at.qe.skeleton.events.SmsNotificationEvent;
import at.qe.skeleton.services.SmsNotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SmsNotificationEventListener {

    private final SmsNotificationService smsNotificationService;

    public SmsNotificationEventListener(SmsNotificationService smsNotificationService) {
        this.smsNotificationService = smsNotificationService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSmsNotificationEvent(SmsNotificationEvent<?> event) {
        smsNotificationService.sendSms(event.getNotificationId(),event.getPayload());
    }
}

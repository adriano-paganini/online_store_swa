package at.qe.skeleton.listeners;

import at.qe.skeleton.events.SmsNotificationEvent;
import at.qe.skeleton.services.SmsNotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SmSNotificationEventListener {

    private final SmsNotificationService smsNotificationService;

    public SmSNotificationEventListener(SmsNotificationService smsNotificationService) {
        this.smsNotificationService = smsNotificationService;
    }

    @EventListener
    public void handleSmsNotificationEvent(SmsNotificationEvent event) {
        smsNotificationService.sendSms(event.getNotification());
    }
}

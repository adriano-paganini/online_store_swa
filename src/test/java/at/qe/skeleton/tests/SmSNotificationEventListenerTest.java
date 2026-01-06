package at.qe.skeleton.tests;

import at.qe.skeleton.events.SmsNotificationEvent;
import at.qe.skeleton.listeners.SmSNotificationEventListener;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.services.SmsNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {SmSNotificationEventListener.class})
@Import(AsyncConfigTest.class)
class SmSNotificationEventListenerTest {

    @Autowired
    ApplicationEventPublisher publisher;

    @MockitoBean
    SmsNotificationService smsNotificationService;

    @Test
    void shouldSendSmsWhenSmsNotificationEventIsPublished() {
        Notification n = new Notification(123L, "msg", NotificationType.SMS);
        Subscription s = new Subscription();

        publisher.publishEvent(new SmsNotificationEvent(n, s));

        verify(smsNotificationService, times(1)).sendSms(n);
        verifyNoMoreInteractions(smsNotificationService);
    }
}

package at.qe.skeleton.tests;

import at.qe.skeleton.events.Payload;
import at.qe.skeleton.events.PayloadInterface;
import at.qe.skeleton.events.SmsNotificationEvent;
import at.qe.skeleton.listeners.SmsNotificationEventListener;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.services.SmsNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {SmsNotificationEventListener.class})
@Import(AsyncTestConfig.class)
class SmsNotificationEventListenerTest {

    @Autowired
    SmsNotificationEventListener listener;

    @MockitoBean
    SmsNotificationService smsNotificationService;

    /** Minimal payload implementation for the event */
    static class TestPayloadInfo implements PayloadInterface {
        @Override
        public String getPayloadSubjectLine() {
            return "test";
        }
    }

    @Test
    void shouldSendSmsWhenSmsNotificationEventIsHandled() {
        Notification n = new Notification(123L, "msg", NotificationType.SMS);

        Payload<TestPayloadInfo> payload = new Payload<>(new TestPayloadInfo());
        SmsNotificationEvent<TestPayloadInfo> event = new SmsNotificationEvent<>(n, payload);

        // Direct call avoids needing a TransactionManager/commit for AFTER_COMMIT
        listener.handleSmsNotificationEvent(event);

        verify(smsNotificationService, times(1)).sendSms(eq(n.getId()), same(payload));
        verifyNoMoreInteractions(smsNotificationService);
    }
}

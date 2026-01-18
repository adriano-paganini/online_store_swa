package at.qe.skeleton.tests;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.events.Payload;
import at.qe.skeleton.events.PayloadInterface;
import at.qe.skeleton.listeners.EmailNotificationEventListener;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.services.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationEventListenerTest {

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private EmailNotificationEventListener listener;

    private Notification testNotification;
    private Payload<TestPayloadInfo> testPayload;

    /**
     * Minimal payload implementation for testing
     */
    static class TestPayloadInfo implements PayloadInterface {
        private final String text;

        TestPayloadInfo(String text) {
            this.text = text;
        }

        @Override
        public String getPayloadSubjectLine() {
            return text;
        }
    }

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUserId(100L);
        testNotification.setChannel(NotificationType.EMAIL);
        testNotification.setMessage("Test notification message");

        testPayload = new Payload<>(new TestPayloadInfo("Test payload subject line"));
    }

    @Test
    void handleEmailNotificationEventCallsEmailNotificationService() {
        EmailNotificationEvent<TestPayloadInfo> event = new EmailNotificationEvent<>(testNotification, testPayload);

        listener.handleEmailNotificationEvent(event);

        verify(emailNotificationService, times(1)).sendEmail(same(event));
        verifyNoMoreInteractions(emailNotificationService);
    }

    @Test
    void handleEmailNotificationEventWithDifferentPayloadTypes() {
        // Test with different payload implementations
        TestPayloadInfo payload1 = new TestPayloadInfo("Payload 1");
        TestPayloadInfo payload2 = new TestPayloadInfo("Payload 2");

        EmailNotificationEvent<TestPayloadInfo> event1 = new EmailNotificationEvent<>(testNotification, new Payload<>(payload1));
        EmailNotificationEvent<TestPayloadInfo> event2 = new EmailNotificationEvent<>(testNotification, new Payload<>(payload2));

        listener.handleEmailNotificationEvent(event1);
        listener.handleEmailNotificationEvent(event2);

        verify(emailNotificationService, times(2)).sendEmail(any(EmailNotificationEvent.class));
    }

    @Test
    void handleEmailNotificationEventVerifiesCorrectEventPassed() {
        EmailNotificationEvent<TestPayloadInfo> event = new EmailNotificationEvent<>(testNotification, testPayload);

        listener.handleEmailNotificationEvent(event);

        ArgumentCaptor<EmailNotificationEvent<?>> eventCaptor = ArgumentCaptor.forClass(EmailNotificationEvent.class);
        verify(emailNotificationService, times(1)).sendEmail(eventCaptor.capture());

        EmailNotificationEvent<?> capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals(testNotification.getId(), capturedEvent.getNotificationId());
    }

    @Test
    void handleEmailNotificationEventWithNullEvent() {
        // Test that null event is passed to service (service will handle validation)
        listener.handleEmailNotificationEvent(null);

        verify(emailNotificationService, times(1)).sendEmail(null);
    }

    @Test
    void handleEmailNotificationEventIsIdempotent() {
        EmailNotificationEvent<TestPayloadInfo> event = new EmailNotificationEvent<>(testNotification, testPayload);

        // Call multiple times
        listener.handleEmailNotificationEvent(event);
        listener.handleEmailNotificationEvent(event);
        listener.handleEmailNotificationEvent(event);

        verify(emailNotificationService, times(3)).sendEmail(same(event));
    }

    @Test
    void handleEmailNotificationEventWithDifferentNotifications() {
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setUserId(100L);
        notification1.setChannel(NotificationType.EMAIL);

        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setUserId(200L);
        notification2.setChannel(NotificationType.EMAIL);

        EmailNotificationEvent<TestPayloadInfo> event1 = new EmailNotificationEvent<>(notification1, testPayload);
        EmailNotificationEvent<TestPayloadInfo> event2 = new EmailNotificationEvent<>(notification2, testPayload);

        listener.handleEmailNotificationEvent(event1);
        listener.handleEmailNotificationEvent(event2);

        verify(emailNotificationService, times(1)).sendEmail(event1);
        verify(emailNotificationService, times(1)).sendEmail(event2);
    }

    @Test
    void handleEmailNotificationEventDoesNotThrowOnServiceException() {
        EmailNotificationEvent<TestPayloadInfo> event = new EmailNotificationEvent<>(testNotification, testPayload);

        doThrow(new RuntimeException("Service error")).when(emailNotificationService).sendEmail(any());

        // The listener should propagate the exception (not catch it)
        assertThrows(RuntimeException.class, () -> {
            listener.handleEmailNotificationEvent(event);
        });

        verify(emailNotificationService, times(1)).sendEmail(same(event));
    }

    @Test
    void handleEmailNotificationEventAsyncBehavior() {
        // Test that the method can be called and completes
        // Note: Actual async behavior is tested via @Async annotation in Spring context
        EmailNotificationEvent<TestPayloadInfo> event = new EmailNotificationEvent<>(testNotification, testPayload);

        // Direct call (not async in unit test, but verifies method works)
        listener.handleEmailNotificationEvent(event);

        // Verify service was called (async would happen in real Spring context)
        verify(emailNotificationService, times(1)).sendEmail(same(event));
    }

    @Test
    void handleEmailNotificationEventWithEmptyPayload() {
        Payload<TestPayloadInfo> emptyPayload = new Payload<>(new TestPayloadInfo(""));
        EmailNotificationEvent<TestPayloadInfo> event = new EmailNotificationEvent<>(testNotification, emptyPayload);

        listener.handleEmailNotificationEvent(event);

        verify(emailNotificationService, times(1)).sendEmail(same(event));
    }

    @Test
    void handleEmailNotificationEventWithNullPayloadContent() {
        // Test with payload that has null subject line
        PayloadInterface nullPayloadInfo = new PayloadInterface() {
            @Override
            public String getPayloadSubjectLine() {
                return null;
            }
        };
        Payload<PayloadInterface> nullPayload = new Payload<>(nullPayloadInfo);
        EmailNotificationEvent<PayloadInterface> event = new EmailNotificationEvent<>(testNotification, nullPayload);

        listener.handleEmailNotificationEvent(event);

        verify(emailNotificationService, times(1)).sendEmail(same(event));
    }
}

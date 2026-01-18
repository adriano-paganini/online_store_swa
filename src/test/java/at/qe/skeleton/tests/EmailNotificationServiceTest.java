package at.qe.skeleton.tests;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.events.Payload;
import at.qe.skeleton.events.PayloadInterface;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.EmailNotificationService;
import at.qe.skeleton.services.NotificationService;
import at.qe.skeleton.services.UserxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailNotificationServiceTest {

    @Autowired
    private EmailNotificationService emailNotificationService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private UserxService userxService;

    private static final class TestPayloadInfo implements PayloadInterface {
        private final String text;

        private TestPayloadInfo(String text) {
            this.text = text;
        }

        @Override
        public String getPayloadSubjectLine() {
            return text;
        }
    }

    @Test
    public void testSendRealEmailThroughGMX() {
        Userx recipient = new Userx();
        recipient.setId(1L);
        recipient.setUsername("testuser");
        recipient.setEmail("PaganiniAdriano1@gmail.com");

        Notification notification = new Notification();
        notification.setId(500L);
        notification.setUserId(recipient.getId());
        notification.setChannel(NotificationType.EMAIL);

        notification.setMessage("Test mail");
        notification.setStatus(NotificationStatus.QUEUED);
        notification.setTimestamp(LocalDateTime.now());

        when(notificationService.getNotificationById(500L)).thenReturn(notification);
        when(userxService.getUserById(1L)).thenReturn(recipient);

        Payload<TestPayloadInfo> payload =
                new Payload<>(new TestPayloadInfo("If you read this, someone is working on your group Project!"));

        EmailNotificationEvent<TestPayloadInfo> event = new EmailNotificationEvent<>(notification, payload);

        emailNotificationService.sendEmail(event);

        verify(notificationService, atLeastOnce()).updateNotificationStatus(any(), eq(notification));
    }
}
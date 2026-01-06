package at.qe.skeleton.tests;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.model.*;
import at.qe.skeleton.services.EmailNotificationService;
import at.qe.skeleton.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

@SpringBootTest
public class EmailNotificationServiceTest {

    @Autowired
    private EmailNotificationService emailNotificationService;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    public void testSendRealEmailThroughGMX() {
        Userx recipient = new Userx();
        recipient.setUsername("testuser");
        recipient.setEmail("PaganiniAdriano1@gmail.com");

        Product product = new Product();
        product.setName("Premium Tree");

        Subscription subscription = new Subscription();
        subscription.setUser(recipient);
        subscription.setProduct(product);

        Notification notification = new Notification();
        notification.setChannel(NotificationType.EMAIL);
        notification.setMessage("Test-Contents: The Price for " + product.getName() + " fell!");
        notification.setTimestamp(LocalDateTime.now());

        EmailNotificationEvent event = new EmailNotificationEvent(notification, subscription);

        emailNotificationService.sendEmail(event);
    }
}
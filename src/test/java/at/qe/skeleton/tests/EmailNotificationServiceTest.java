package at.qe.skeleton.tests;

import at.qe.skeleton.events.EmailNotificationEvent;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.SubscriptionRepository;
import at.qe.skeleton.services.EmailNotificationService;
import at.qe.skeleton.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@SpringBootTest
public class EmailNotificationServiceTest {

    @Autowired
    private EmailNotificationService emailNotificationService;

    @MockitoBean
    private NotificationService notificationService;
    @MockitoBean
    private SubscriptionRepository subscriptionRepository;

    @Test
    public void testSendRealEmailThroughGMX() {
        Userx recipient = new Userx();
        recipient.setUsername("testuser");
        recipient.setEmail("PaganiniAdriano1@gmail.com");

        Product product = new Product();
        product.setName("Premium Tree");

        Subscription subscription = new Subscription();
        subscription.setId(500L);
        subscription.setUser(recipient);
        subscription.setProduct(product);

        Notification notification = new Notification();
        notification.setId(500L);
        notification.setChannel(NotificationType.EMAIL);
        notification.setMessage("Test-Contents: The Price for " + product.getName() + " fell!");
        notification.setTimestamp(LocalDateTime.now());


        when(notificationService.getNotificationById(anyLong())).thenReturn(notification);
        when(subscriptionRepository.findById(500L)).thenReturn(Optional.of(subscription));
        EmailNotificationEvent event = new EmailNotificationEvent(notification, subscription);
        emailNotificationService.sendEmail(event);
    }
}
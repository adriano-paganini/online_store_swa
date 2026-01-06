package at.qe.skeleton.tests;

import at.qe.skeleton.events.ProductEvent;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.NotificationRepository;
import at.qe.skeleton.services.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockitoBean
    private NotificationRepository notificationRepository;

    private Userx testUser;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void testUpdateNotificationStatus() {
        Notification notification = new Notification(1L, "Test Message", NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.QUEUED);

        notificationService.updateNotificationStatus(NotificationStatus.SENT, notification);

        Assertions.assertEquals(NotificationStatus.SENT, notification.getStatus());
        verify(notificationRepository).save(notification);
    }

    @Test
    void testCreateNotification() {
        ProductEvent<?> mockEvent = Mockito.mock(ProductEvent.class);
        when(mockEvent.getMessage()).thenReturn("Product Price Changed");

        Notification result = notificationService.createNotification(1L, NotificationType.EMAIL, mockEvent);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Product Price Changed", result.getMessage());
        Assertions.assertEquals(NotificationType.EMAIL, result.getChannel());
        Assertions.assertEquals(1L, result.getUserId());

        verify(notificationRepository).save(ArgumentMatchers.any(Notification.class));
    }

    @Test
    void testGetUserNotifications() {
        int page = 0;
        int limit = 10;
        String sort = "timestamp,desc";

        Notification n = new Notification(1L, "Msg", NotificationType.EMAIL);
        Page<Notification> notificationPage = new PageImpl<>(List.of(n));

        when(notificationRepository.findByUserWithFilter(
                Mockito.eq(testUser.getId()),
                Mockito.eq(NotificationStatus.SENT),
                Mockito.eq(NotificationType.EMAIL),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(notificationPage);

        Page<Notification> result = notificationService.getUserNotifications(
                testUser, page, limit, NotificationStatus.SENT, NotificationType.EMAIL, sort);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(notificationRepository).findByUserWithFilter(
                Mockito.eq(testUser.getId()), Mockito.any(), Mockito.any(), pageableCaptor.capture());

        Assertions.assertTrue(Objects.requireNonNull(pageableCaptor.getValue().getSort().getOrderFor("timestamp")).isDescending());
    }

    @Test
    void testParseSortWithInvalidField() {
        int page = 0;
        int limit = 10;
        String invalidSort = "unknownField,asc";

        notificationService.getUserNotifications(testUser, page, limit, null, null, invalidSort);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(notificationRepository).findByUserWithFilter(
                Mockito.anyLong(), Mockito.any(), Mockito.any(), pageableCaptor.capture());

        Assertions.assertEquals("timestamp", pageableCaptor.getValue().getSort().get().findFirst().get().getProperty());
    }
}
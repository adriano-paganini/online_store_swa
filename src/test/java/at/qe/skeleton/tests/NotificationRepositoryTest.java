package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.NotificationRepository;
import at.qe.skeleton.repositories.UserxRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


@DataJpaTest
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserxRepository userRepository;

    private Userx testUser;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setUsername("notifUser");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        for (int i = 0; i < 10; i++) {
            NotificationType type = (i < 5) ? NotificationType.EMAIL : NotificationType.SMS;
            String msg = "Test Message " + i;

            Notification n = new Notification(testUser.getId(), msg, type);

            if (i == 3 || i == 4 || i == 7 || i == 8 || i == 9) {
                n.setStatus(NotificationStatus.SENT);
            } else {
                n.setStatus(NotificationStatus.QUEUED);
            }

            notificationRepository.save(n);
        }
    }

    @Test
    void testFindByUserWithFilterNoFilters() {
        Page<Notification> result = notificationRepository.findByUserWithFilter(
                testUser.getId(), null, null, PageRequest.of(0, 20));

        Assertions.assertEquals(10, result.getTotalElements(), "Should find all 10 users");
    }

    @Test
    void testFindByUserWithFilterStatusFilter() {
        Page<Notification> queued = notificationRepository.findByUserWithFilter(
                testUser.getId(), NotificationStatus.QUEUED, null, PageRequest.of(0, 10));

        Assertions.assertEquals(5, queued.getTotalElements());
        Assertions.assertTrue(queued.getContent().stream()
                .allMatch(n -> n.getStatus() == NotificationStatus.QUEUED));
    }

    @Test
    void testFindByUserWithFilterChannelFilter() {
        Page<Notification> emails = notificationRepository.findByUserWithFilter(
                testUser.getId(), null, NotificationType.EMAIL, PageRequest.of(0, 10));

        Assertions.assertEquals(5, emails.getTotalElements());
        Assertions.assertTrue(emails.getContent().stream()
                .allMatch(n -> n.getChannel() == NotificationType.EMAIL));
    }

    @Test
    void testFindByUserWithFilterCombinedFilter() {
        Page<Notification> filtered = notificationRepository.findByUserWithFilter(
                testUser.getId(), NotificationStatus.SENT, NotificationType.SMS, PageRequest.of(0, 10));

        Assertions.assertEquals(3, filtered.getTotalElements());
        Assertions.assertTrue(filtered.getContent().stream()
                .allMatch(n -> n.getStatus() == NotificationStatus.SENT && n.getChannel() == NotificationType.SMS));
    }

    @Test
    void testFindByUserWithFilterPagination() {
        PageRequest pageable = PageRequest.of(0, 3);
        Page<Notification> page = notificationRepository.findByUserWithFilter(
                testUser.getId(), null, null, pageable);

        Assertions.assertEquals(10, page.getTotalElements());
        Assertions.assertEquals(3, page.getContent().size());
    }

    @Test
    void testFindByUserWithFilterEmptyResultForOtherUser() {
        Page<Notification> result = notificationRepository.findByUserWithFilter(
                999L, null, null, PageRequest.of(0, 10));

        Assertions.assertTrue(result.isEmpty());
    }
}
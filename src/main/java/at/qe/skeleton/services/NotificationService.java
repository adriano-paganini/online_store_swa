package at.qe.skeleton.services;

import at.qe.skeleton.Helpers.SortHelper;
import at.qe.skeleton.events.Payload;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service for managing the lifecycle of {@link Notification} entities.
 * <p>
 * This service provides methods for creating new notifications, updating their status,
 * and retrieving paginated notification lists for specific users with filtering capabilities.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SortHelper sortHelper;

    public NotificationService(NotificationRepository notificationRepository, SortHelper sortHelper) {
        this.notificationRepository = notificationRepository;
        this.sortHelper = sortHelper;
    }

    // Fetches a specific Notification by its unique identifier
    public Notification getNotificationById(Long id){
        return notificationRepository.getNotificationById(id);
    }

    @Transactional
    public void updateNotificationStatus(NotificationStatus status, Notification notification){
        // Persist the new status (e.g., SENT, FAILED) to the database
        notification.setStatus(status);
        notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotification(Long userId, NotificationType channel, Payload<?> event) {
        // Instantiate a new notification based on user, delivery channel, and event subject information
        Notification notification = new Notification(userId, event.getPayloadInfo().getPayloadSubjectLine(), channel);

        // Persist the notification to generate an ID and audit trail
        notificationRepository.save(notification);

        return notification;
    }

    public Page<Notification> getUserNotifications(
            Userx user, int page, int limit, NotificationStatus status, NotificationType channel, String sort) {

        // Utilize SortHelper to validate sort fields, allowing "channel" and "status", falling back to "timestamp"
        Sort sortObj = sortHelper.parseSort(sort,Notification.class,
                field -> List.of("channel","status").contains(field),
                "timestamp");

        // Create a pageable object with the extracted sort and pagination parameters
        Pageable pageable = PageRequest.of(page, limit, sortObj);

        // Query the repository for filtered and paginated user notifications
        return notificationRepository.findByUserWithFilter(user.getId(), status, channel, pageable);
    }
}
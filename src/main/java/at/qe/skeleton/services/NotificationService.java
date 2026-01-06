package at.qe.skeleton.services;

import at.qe.skeleton.events.ProductEvent;
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

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void updateNotificationStatus(NotificationStatus status, Notification notification){
        notification.setStatus(status);
        notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotification(Long userId, NotificationType chanel, ProductEvent<?> event) {
        Notification notification = new Notification(userId, event.getMessage(), chanel);

        notificationRepository.save(notification);
        return notification;
    }

    public Page<Notification> getUserNotifications(
            Userx user, int page, int limit, NotificationStatus status, NotificationType channel, String sort) {

        Sort sortObj = parseSort(sort);

        Pageable pageable = PageRequest.of(page, limit, sortObj);

        return notificationRepository.findByUserWithFilter(user.getId(), status, channel, pageable);
    }

    private Sort parseSort(String sortString) {
        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.DESC, "timestamp");
        }

        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        Sort.Direction sortDirection = "asc".equals(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        if (!isValidSortField(field)) {
            field = "timestamp";
        }

        return Sort.by(sortDirection, field);
    }

    private boolean isValidSortField(String field) {
        return "timestamp".equals(field) || "channel".equals(field) || "status".equals(field);
    }
}

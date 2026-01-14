package at.qe.skeleton.services;

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

import static at.qe.skeleton.Helpers.SortHelper.parseSort;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification getNotificationById(Long id){
        return notificationRepository.getNotificationById(id);
    }

    @Transactional
    public void updateNotificationStatus(NotificationStatus status, Notification notification){
        notification.setStatus(status);
        notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotification(Long userId, NotificationType chanel, Payload<?> event) {
        Notification notification = new Notification(userId, event.getPayloadInfo().getPayloadSubjectLine(), chanel);

        notificationRepository.save(notification);
        return notification;
    }

    public Page<Notification> getUserNotifications(
            Userx user, int page, int limit, NotificationStatus status, NotificationType channel, String sort) {

        Sort sortObj = parseSort(sort,
                field -> List.of("channel","status").contains(field),
                "timestamp");

        Pageable pageable = PageRequest.of(page, limit, sortObj);

        return notificationRepository.findByUserWithFilter(user.getId(), status, channel, pageable);
    }
}

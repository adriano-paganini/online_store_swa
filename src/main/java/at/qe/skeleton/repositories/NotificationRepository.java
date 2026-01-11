package at.qe.skeleton.repositories;


import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends AbstractRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
            "AND (:status IS NULL OR n.status = :status) " +
            "AND (:channel IS NULL OR n.channel = :channel)")
    Page<Notification> findByUserWithFilter(
            @Param("userId") Long userId,
            @Param("status") NotificationStatus status,
            @Param("channel") NotificationType channel,
            Pageable pageable
    );


    Notification getNotificationById(Long id);
}

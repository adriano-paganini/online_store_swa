package at.qe.skeleton.repositories;


import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository interface for {@link Notification} entities.
 * <p>
 * This repository provides specialized query methods to:
 * <ul>
 * <li>Retrieve a paginated list of notifications for a specific user.</li>
 * <li>Apply filters based on notification status (e.g., QUEUED, SENT) and delivery channel (e.g., EMAIL, SMS).</li>
 * <li>Fetch a single notification by its unique identifier.</li>
 * </ul>
 */
public interface NotificationRepository extends AbstractRepository<Notification, Long> {

    // Retrieves a paginated list of notifications for a user, filtered by optional status and channel parameters.
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
            "AND (:status IS NULL OR n.status = :status) " +
            "AND (:channel IS NULL OR n.channel = :channel)")
    Page<Notification> findByUserWithFilter(
            @Param("userId") Long userId,
            @Param("status") NotificationStatus status,
            @Param("channel") NotificationType channel,
            Pageable pageable
    );

    // Retrieves a specific notification entity by its ID.
    Notification getNotificationById(Long id);
}

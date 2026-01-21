package at.qe.skeleton.repositories;

import at.qe.skeleton.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository interface for {@link Subscription} entities.
 * <p>
 * This repository provides specialized query methods to:
 * <ul>
 * <li>Retrieve subscriptions by user or product.</li>
 * <li>Find unique subscriptions matching a specific product and subscription type (e.g., price drop).</li>
 * <li>Support paginated and filtered searches for user subscriptions based on multiple types and notification channels.</li>
 * </ul>
 */
public interface SubscriptionRepository extends AbstractRepository<Subscription, Long> {

    // Retrieves all subscriptions associated with a specific user.
    List<Subscription> findByUser(Userx user);

    // Finds all subscriptions for a specific product that have a specific type (e.g., STOCK_UPDATE) enabled.
    @Query("SELECT DISTINCT s " +
            "FROM Subscription s " +
            "WHERE s.product.id = :productId " +
            "AND :subscriptionType MEMBER OF s.types")
    List<Subscription> findByProductAndType(
            @Param("productId") Long productId,
            @Param("subscriptionType") SubscriptionType subscriptionType);

    // Retrieves all subscriptions associated with a specific product.
    List<Subscription> findByProduct(Product product);

    // Provides a paginated list of subscriptions for a user, filtered by optional arrays of types and channels.
    @Query("SELECT DISTINCT s FROM Subscription s " +
            "LEFT JOIN s.types t " +
            "LEFT JOIN s.channels c " +
            "WHERE s.user.id = :userId " +
            "AND (:types IS NULL OR t IN :types) " +
            "AND (:channels IS NULL OR c IN :channels)")
    Page<Subscription> findByUserWithFilter(
            @Param("userId") Long userId,
            @Param("types") SubscriptionType[] types,
            @Param("channels") NotificationType[] channels,
            Pageable pageable
    );

    // Finds a single subscription (if it exists) by both user ID and product ID.
    @Query("SELECT s FROM  Subscription s WHERE s.user.id = :userId AND s.product.id = :productId")
    Optional<Subscription> findByUserAndProduct(
            @Param("userId") Long userId,
            @Param("productId") Long productId
    );

}

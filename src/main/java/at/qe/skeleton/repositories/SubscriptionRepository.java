package at.qe.skeleton.repositories;

import at.qe.skeleton.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends AbstractRepository<Subscription,Long>{

    List<Subscription> findByUser(Userx user);

    @Query("SELECT DISTINCT s " +
            "FROM Subscription s " +
            "WHERE s.product.id = :productId " +
            "AND :subscriptionType MEMBER OF  s.types")
    List<Subscription> findByProductAndType(
            @Param("productId") Long productId,
            @Param("subscriptionType") SubscriptionType subscriptionType);

    List<Subscription> findByProduct(Product product);

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


    @Query("SELECT s FROM  Subscription s WHERE s.user.id = :userId AND s.product.id = :productId")
    Optional<Subscription>  findByUserAndProduct(
            @Param("userId") Long userId,
            @Param("productId") Long productId
    );

}

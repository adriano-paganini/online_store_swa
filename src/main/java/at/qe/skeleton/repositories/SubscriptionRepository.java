package at.qe.skeleton.repositories;

import at.qe.skeleton.model.SubscriptionType;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.Userx;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRepository extends AbstractRepository<Subscription,Long>{

    List<Subscription> findByUser(Userx user);

    @Query("SELECT s " +
            "FROM Subscription s " +
            "WHERE s.product.id = :productId " +
            "AND :subscriptionType MEMBER OF  s.types")
    List<Subscription> findByProductAndType(
            @Param("productId") Long productId,
            @Param("subscriptionType") SubscriptionType subscriptionType);

    List<Subscription> findByProduct(Product product);
}

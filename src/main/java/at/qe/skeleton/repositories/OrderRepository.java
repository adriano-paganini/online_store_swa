package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.model.Userx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends AbstractRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String number);
    Page<Order> findByUser(Userx user, Pageable pageable);
    Page<Order> findByUserAndStatus(
            Userx user,
            OrderStatus status,
            Pageable pageable
    );
    //load items, to avoid lazy-loading exception
    @Query("""
    select o from Order o
    left join fetch o.items
    where o.orderNumber = :orderNumber
  """)
    Optional<Order> findByOrderNumberWithItems(String orderNumber);
}

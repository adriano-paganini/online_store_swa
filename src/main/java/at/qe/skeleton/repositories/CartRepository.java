package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Cart;
import at.qe.skeleton.model.Userx;
import java.util.Optional;

public interface CartRepository extends AbstractRepository<Cart, Long> {

    Optional<Cart> findByUser(Userx user);
}


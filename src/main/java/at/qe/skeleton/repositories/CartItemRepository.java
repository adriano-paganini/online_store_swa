package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Cart;
import at.qe.skeleton.model.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends AbstractRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
}


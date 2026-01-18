package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.CartItemCreateDTO;
import at.qe.skeleton.model.CartItem;
import org.springframework.stereotype.Service;

@Service
public class CartItemCreateMapper {
    
    public CartItem mapFrom(CartItemCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        CartItem cartItem = new CartItem();
        cartItem.setProductId(dto.productId());
        cartItem.setQuantity(dto.quantity());
        
        return cartItem;
    }
}

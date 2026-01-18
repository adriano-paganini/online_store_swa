package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.CartDTO;
import at.qe.skeleton.dtos.CartItemDTO;
import at.qe.skeleton.dtos.CartItemUpdateDTO;
import at.qe.skeleton.model.Cart;
import at.qe.skeleton.model.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CartMapper {

    public CartDTO mapTo(Cart cart) {
        if (cart == null) {
            return new CartDTO(List.of());
        }

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::mapItemTo)
                .collect(Collectors.toList());

        return new CartDTO(itemDTOs);
    }

    private CartItemDTO mapItemTo(CartItem item) {
        if (item == null) {
            return null;
        }

        return new CartItemDTO(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getAppliedDiscount(),
                item.getCurrentPrice()
        );
    }

    public void apply(CartItem cartItem, CartItemUpdateDTO dto) {
        if (dto.quantity() != null) {
            cartItem.setQuantity(dto.quantity());
        }
        if (dto.appliedDiscount() != null) {
            cartItem.setAppliedDiscount(dto.appliedDiscount());
        }
    }
}


package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.*;
import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderAddress;
import at.qe.skeleton.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderDTO toDto(Order order) {
        return new OrderDTO(
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotal(),
                order.getTimestamp(),
                mapItems(order.getItems()),
                mapAddress(order.getShippingAddress()),
                mapAddress(order.getBillingAddress())
        );
    }

    private List<OrderItemDTO> mapItems(List<OrderItem> items) {
        return items.stream()
                .map(this::toItemDto)
                .toList();
    }

    private OrderItemDTO toItemDto(OrderItem item) {
        return new OrderItemDTO(
                item.getProductId(),
                item.getQuantity(),
                item.getPriceAtPurchase(),
                item.getAppliedDiscount()
        );
    }

    private AddressDTO mapAddress(OrderAddress address) {
        if (address == null) {
            return null;
        }
        return new AddressDTO(
                null,
                address.getCountry(),
                address.getCity(),
                address.getPostalCode(),
                address.getStreet(),
                address.getNumber(),
                address.getExtra()
        );
    }
}

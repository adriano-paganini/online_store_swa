package at.qe.skeleton.dtos;

import at.qe.skeleton.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO (
        String orderNumber,
        OrderStatus status,
        Double total,
        LocalDateTime timestamp,
        List<OrderItemDTO> items,
        AddressDTO shippingAddress,
        AddressDTO billingAddress
) {}

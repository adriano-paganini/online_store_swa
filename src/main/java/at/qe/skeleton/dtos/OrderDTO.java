package at.qe.skeleton.dtos;

import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.model.ShippingMethod;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO (
        String orderNumber,
        OrderStatus status,
        Double total,
        LocalDateTime timestamp,
        List<OrderItemDTO> items,
        AddressDTO shippingAddress,
        AddressDTO billingAddress,
        ShippingMethod shippingMethod,
        LocalDateTime paidAt,
        String transactionId
) {}

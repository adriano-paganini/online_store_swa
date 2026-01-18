package at.qe.skeleton.services;

import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.model.ShippingMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderLifecycleService {

    public boolean applyResolvedStatus(Order order, LocalDateTime now) {
        OrderStatus resolved = resolveStatus(order, now);

        if (resolved != order.getStatus()) {
            order.setStatus(resolved);
            return true;
        }
        return false;
    }

    public OrderStatus resolveStatus(Order order, LocalDateTime now) {

        if (order.getTimestamp() == null) {
            return order.getStatus();
        }
        if (order.getStatus() == null) {
            return OrderStatus.PENDING;
        }

        if (order.getStatus() == OrderStatus.PAID) {
            if (order.getTimestamp().plusHours(12).isBefore(now)) {
                return OrderStatus.SHIPPING;
            }
        }

        if (order.getStatus() == OrderStatus.SHIPPING) {
            long hours = shippingHours(order.getShippingMethod());

            if (order.getTimestamp().plusHours(12 + hours).isBefore(now)) {
                return OrderStatus.DELIVERED;
            }
        }

        return order.getStatus();
    }


    private long shippingHours(ShippingMethod method) {
        return switch (method) {
            case FAIRY_DUST_DISPATCH -> 24;
            case CARRIER_PIGEON -> 72;
            case WELL_FIGURE_IT_OUT -> 168;
        };
    }
}

package at.qe.skeleton.services;

import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.model.ShippingMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for managing OrderStatus updates based on time.
 * <p>
 * This service handles the logic for applying the correct OrderStatus.
 * The times for status updates are stubbed, since we do not actually ship products.
 */
@Service
public class OrderLifecycleService {

    /**
     * Apply the applicable OrderStatus based on LocalDateTime.
     *
     * @param order the order to resolve the status
     * @param now the LocalDateTime on which the update is based
     * @return {@code true} if the OrderStatus is updated,
     * {@code false} if the resolved status is the already set OrderStatus
     */
    public boolean applyResolvedStatus(Order order, LocalDateTime now) {
        OrderStatus resolved = resolveStatus(order, now);

        if (resolved != order.getStatus()) {
            order.setStatus(resolved);
            return true;
        }
        return false;
    }

    /**
     * Identifies the applicable OrderStatus based on a LocalDateTime.
     * <p>
     * This method makes sure on retrieval in OrderService the status is correctly applied.
     * This is stubbed, because we do not actually dispatch orders, so the status updates are based on time intervals.
     *
     * @param order the order to resolve the status
     * @param now the LocalDateTime on which the update is based
     * @return the applicable OrderStatus
     */
    public OrderStatus resolveStatus(Order order, LocalDateTime now) {

        if (order.getTimestamp() == null) {
            return order.getStatus();
        }
        if (order.getStatus() == null) {
            return OrderStatus.PENDING;
        }

        // since we do not dispatch orders 12 hours is the stubbed time
        // when an order will be processed and go into shipping
        if (order.getStatus() == OrderStatus.PAID && order.getTimestamp().plusHours(12).isBefore(now)) {
            return OrderStatus.SHIPPING;
        }

        if (order.getStatus() == OrderStatus.SHIPPING) {
            long hours = shippingHours(order.getShippingMethod());

            // since we do not dispatch orders 12 hours is the stubbed time
            // when a packet arrives and will be set to be delivered
            if (order.getTimestamp().plusHours(12 + hours).isBefore(now)) {
                return OrderStatus.DELIVERED;
            }
        }

        return order.getStatus();
    }

    /**
     * Get the shipping time based on the shipping method.
     *
     * @param method the selected shipping method
     * @return the shipping time
     */
    private long shippingHours(ShippingMethod method) {
        return switch (method) {
            case FAIRY_DUST_DISPATCH -> 24;
            case CARRIER_PIGEON -> 72;
            case WELL_FIGURE_IT_OUT -> 168;
        };
    }
}

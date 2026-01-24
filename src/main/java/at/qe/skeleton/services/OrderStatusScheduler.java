package at.qe.skeleton.services;

import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.repositories.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;
    private final OrderLifecycleService orderLifecycleService;

    public OrderStatusScheduler(OrderRepository orderRepository, OrderLifecycleService orderLifecycleService) {
        this.orderRepository = orderRepository;
        this.orderLifecycleService = orderLifecycleService;
    }

    /**
     * Checks periodically for orders that need a status update.
     * Runs every 10 seconds to catch the 1 min and 2 min thresholds reasonably fast.
     */
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void updateOrderStatuses() {
        // Fetch orders that are PAID or SHIPPING (since these are the ones that evolve automatically)
        // Note: For a large system, this should be paginated or optimized with a specific query.
        // For this project, fetching all active orders is acceptable.
        
        // We can fetch all, or filter. Let's fetch all active orders to keep it simple with existing repository methods, 
        // or just iterate. The OrderRepository likely has findByUser, but maybe not a generic "findAllActive".
        // Let's rely on findAll for now or add a custom query if needed. 
        // Optimization: Create a query for specific statuses.
        
        List<Order> activeOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.SHIPPING)
                .toList();

        LocalDateTime now = LocalDateTime.now();

        for (Order order : activeOrders) {
            if (orderLifecycleService.applyResolvedStatus(order, now)) {
                orderRepository.save(order);
                // Ideally log this change
                System.out.println("Auto-updated Order " + order.getOrderNumber() + " to " + order.getStatus());
            }
        }
    }
}

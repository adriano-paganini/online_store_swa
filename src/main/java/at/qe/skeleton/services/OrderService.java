package at.qe.skeleton.services;

import at.qe.skeleton.dtos.OrderCreateDTO;
import at.qe.skeleton.events.OrderCompletionEvent;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing orders.
 * <p>
 * This service handles the core business logic for orders, including
 * creation, updates and filtered retrieval.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderLifecycleService orderLifecycleService;
    private final CartService cartService;
    private final ProductService productService;
    private final AuthenticatedUserService authenticatedUserService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderService(
            OrderRepository orderRepository,
            OrderLifecycleService orderLifecycleService,
            CartService cartService,
            ProductService productService,
            AuthenticatedUserService authenticatedUserService,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.orderLifecycleService = orderLifecycleService;
        this.cartService = cartService;
        this.productService = productService;
        this.authenticatedUserService = authenticatedUserService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Updates the OrderStatus.
     * @param orders the paginated orders to apply the updates to
     */
    @Transactional
    protected void applyLifecycleUpdates(Page<Order> orders) {
        LocalDateTime now = LocalDateTime.now();

        orders.forEach(order -> {
            if (orderLifecycleService.applyResolvedStatus(order, now)) {
                orderRepository.save(order);
            }
        });
    }

    /**
     * Retrieves the current authenticated users paginated orders with optional filtering and sorting.
     *
     * @param status optional filter by OrderStatus
     * @param page the page index
     * @param limit the maximum number of orders per page
     * @param sort sort specification
     * @return a page of orders matching the given criteria
     */
    @Transactional
    public Page<Order> getCurrentUserOrders(
            OrderStatus status,
            int page,
            int limit,
            String sort
    ) {
        Sort.Direction direction = Sort.Direction.DESC; // default

        if (sort != null) {
            String[] parts = sort.split(",");
            if (parts.length > 1) {
                direction = Sort.Direction.fromOptionalString(parts[1].toLowerCase())
                        .orElse(Sort.Direction.DESC);
            }
        }

        Userx user = authenticatedUserService.requireAuthenticatedUser();

        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(direction, "timestamp")
        );
        Page<Order> pageResult;

        if (status != null) {
            pageResult = orderRepository.findByUserAndStatus(user, status, pageable);
        } else {
            pageResult = orderRepository.findByUser(user, pageable);
        }

        applyLifecycleUpdates(pageResult);
        return pageResult;
    }

    /**
     * Retrieves an order by OrderNumber.
     *
     * @param orderNumber the OrderNumber of the order
     * @return the order
     * @throws ResponseStatusException 404 if the order does not exist or does not belong to the authenticated user
     */
    public Order getOrderByNumber(String orderNumber) {
        Userx user = authenticatedUserService.requireAuthenticatedUser();

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found"));

        if (!(order.getUser().getId() != null && order.getUser().getId().equals(user.getId()))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        if (orderLifecycleService.applyResolvedStatus(order, LocalDateTime.now())) {
            orderRepository.save(order);
        }

        return order;
    }

    /**
     * Creates a new order.
     *
     * @param orderCreateDTO the OrderCreateDTO with the fields to update
     * @return the updated order
     * @throws ResponseStatusException
     *                  400 if the cart is empty,
     *                  400 if the billing address is {@code null},
     *                  400 if the shipping address is {@code null},
     *                  400 if the shipping method is {@code null}
     */
    @Transactional
    public Order createOrder(OrderCreateDTO orderCreateDTO) {

        Userx user = authenticatedUserService.requireAuthenticatedUser();

        Cart cart = cartService.getCart();
        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }
        // orderCreateDTO is deliberately not mapped via a mapper
        // because it would require to inject services into the mapper
        if (orderCreateDTO.billingAddressId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Billing Address is required.");
        }
        if (orderCreateDTO.shippingAddressId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shipping Address is required.");
        }
        if (orderCreateDTO.shippingMethod() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shipping Method is required.");
        }

        OrderAddress billingAddress = snapshotAddress(user, orderCreateDTO.billingAddressId());
        OrderAddress shippingAddress = snapshotAddress(user, orderCreateDTO.shippingAddressId());

        List<OrderItem> orderItems = createOrderItemsFromCart(cart);
        double total = calculateTotal(orderItems);

        Order order = new Order(
                user,
                orderItems,
                billingAddress,
                shippingAddress,
                orderCreateDTO.shippingMethod(),
                total
        );

        Map<Long,Integer> productIdQuanityMap = new HashMap<>();

        for (OrderItem item: orderItems){
            productIdQuanityMap.put(item.getProductId(),item.getQuantity());
        }
        productService.adjustProductStockWithMap(productIdQuanityMap);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart();
        return savedOrder;
    }

    /**
     * Creates an OrderAddress from an Address.
     *
     * @param user the user the address is assigned to
     * @param addressId the id of the address to snapshot
     * @return the snapshot of the address as OrderAddress
     * @throws ResponseStatusException 404 if the address does not exist
     */
    private OrderAddress snapshotAddress(Userx user, Long addressId) {
        Address address = user.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        return new OrderAddress(
                address.getCountry(),
                address.getCity(),
                address.getPostalCode(),
                address.getStreet(),
                address.getNumber(),
                address.getExtra()
        );
    }

    /**
     * Updates the order on successful payment with transaction id and OrderStatus.
     *
     * @param orderNumber the order number of the order to update
     * @param transactionId the successful transaction id to store in the order
     * @return the updated order
     * @throws ResponseStatusException 404 if order does not exist
     */
    @Transactional
    public Order confirmPayment(String orderNumber, String transactionId) {
        Order order = getOrderByNumber(orderNumber);

        order.setStatus(OrderStatus.PAID);
        order.setTransactionId(transactionId);
        order.setPaidAt(LocalDateTime.now());

        orderRepository.save(order);
        //load order with items, to avoid lazy-loading exception
        Order fullOrder = orderRepository.findByOrderNumberWithItems(order.getOrderNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        applicationEventPublisher.publishEvent(new OrderCompletionEvent(fullOrder));

        return order;
    }

    /**
     * Updates the OrderStatus of an order.
     *
     * @param status the new OrderStatus
     * @param orderNumber the order number of the order to update
     * @return the updated order
     */
    @Transactional
    public Order updateOrderStatus(OrderStatus status, String orderNumber){
        Order order = getOrderByNumber(orderNumber);
        order.setStatus(status);
        orderRepository.save(order);
        return order;
    }

    /**
     * Cancels an order.
     * <p>
     * The OrderStatus is set to status CANCELED.
     * The stock is updated to include the already withdrawn quantity again.
     *
     * @param orderNumber the order number of the order to cancel
     * @return the updated order
     * @throws ResponseStatusException
     *                  400 if the order is already in status SHIPPING or DELIVERED
     *                  and cannot be canceled anymore
     */
    @Transactional
    public Order cancelOrder(String orderNumber) {
        Order order = getOrderByNumber(orderNumber);

        if (order.getStatus() == OrderStatus.SHIPPING ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order can no longer be canceled"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            return order;
        }

        Map<Long,Integer> productIdQuantityMap = new HashMap<>();

        for (OrderItem item: order.getItems()){
            productIdQuantityMap.put(item.getProductId(),-1*item.getQuantity());
        }
        productService.adjustProductStockWithMap(productIdQuantityMap);

        order.setStatus(OrderStatus.CANCELED);
        return orderRepository.save(order);
    }

    /**
     * Creates OrderItems from a cart.
     *
     * @param cart the cart to create the OrderItems from
     * @return a list of OrderItems
     * @throws ResponseStatusException 404 if the product does not exist
     */
    private List<OrderItem> createOrderItemsFromCart(Cart cart) {
        return cart.getItems().stream()
                .map(cartItem -> {
                    Product product = productService.getProductById(cartItem.getProductId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Product not found"));

                    OrderItem item = new OrderItem();
                    item.setProductId(product.getId());
                    item.setProductName(product.getName());
                    item.setPriceAtPurchase(cartItem.getCurrentPrice());

                    double discount = normalizeAndValidateDiscount(
                            cartItem.getAppliedDiscount(),
                            product.getId()
                    );

                    item.setAppliedDiscount(discount);
                    item.setQuantity(cartItem.getQuantity());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Checks if discount is within bounds and makes sure it is a number.
     * <p>
     * Normalizes the discount to be a {@code double} primitive and not {@code Null}.
     * Check if discount is greater than 0.0 (0%) and less than 1.0 (100%).
     *
     * @param rawDiscount discount that should be applied
     * @param productId the id of the product the discount should be applied
     * @return unboxed discount to guarantee discount being not {@code Null}
     * @throws IllegalArgumentException if discount is out of bounds
     */
    private double normalizeAndValidateDiscount(Double rawDiscount, Long productId) {
        double discount = rawDiscount != null ? rawDiscount : 0.0;

        if (discount < 0.0 || discount > 1.0) {
            throw new IllegalArgumentException(
                    "Invalid discount on cart item for product " + productId
            );
        }

        return discount;
    }

    /**
     * Calculate order total price.
     *
     * @param items a complete list of OrderItems of an order
     * @return the total price of the order based on the list of OrderItems
     */
    private double calculateTotal(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> {
                    double price = item.getPriceAtPurchase();
                    double discount = item.getAppliedDiscount() != null
                            ? item.getAppliedDiscount()
                            : 0.0;
                    return (price - price * discount) * item.getQuantity();
                })
                .sum();
    }
}

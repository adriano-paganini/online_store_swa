package at.qe.skeleton.services;

import at.qe.skeleton.dtos.OrderCreateDTO;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final AuthenticatedUserService authenticatedUserService;

    public OrderService(
            OrderRepository orderRepository,
            CartService cartService,
            ProductService productService,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.authenticatedUserService = authenticatedUserService;
    }

    public Page<Order> getCurrentUserOrders(
            OrderStatus status,
            int page,
            int limit
    ) {
        Userx user = requireAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, limit, Sort.by("timestamp").descending());

        if (status != null) {
            return orderRepository.findByUserAndStatus(user, status, pageable);
        }
        return orderRepository.findByUser(user, pageable);
    }


    public Order getOrderByNumber(String orderNumber) {
        Userx user = requireAuthenticatedUser();

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found"
                        )
                );

        if (!order.getUser().getId().equals(user.getId())) {
            // 404 instead of 403 (no leak of existence)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found"
            );
        }

        return order;
    }

    @Transactional
    public Order createOrder(OrderCreateDTO orderCreateDTO) {

        Userx user = requireAuthenticatedUser();

        Cart cart = cartService.getCart();
        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
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
                total
        );

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart();

        return savedOrder;
    }

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

    // TODO: centralize requireAuthenticatedUser() in AuthenticatedUserService
    private Userx requireAuthenticatedUser() {
        Userx user = authenticatedUserService.getAuthenticatedUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return user;
    }

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
                    item.setAppliedDiscount(cartItem.getAppliedDiscount());
                    item.setQuantity(cartItem.getQuantity());
                    return item;
                })
                .collect(Collectors.toList());
    }

    private double calculateTotal(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> {
                    double price = item.getPriceAtPurchase();
                    double discount = item.getAppliedDiscount() != null
                            ? item.getAppliedDiscount()
                            : 0.0;
                    return (price - discount) * item.getQuantity();
                })
                .sum();
    }
}

package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.OrderCreateDTO;
import at.qe.skeleton.dtos.OrderDTO;
import at.qe.skeleton.dtos.PageResponseDTO;
import at.qe.skeleton.mappers.OrderMapper;
import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.services.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing orders of the authenticated user.
 *
 * <p>
 * Provides endpoints to list, retrieve, create, and cancel orders
 * associated with the currently authenticated user.
 * </p>
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    /**
     * Retrieves a paginated list of orders for the authenticated user.
     *
     * <p>Supports optional filtering by order status and sorting.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - orders successfully retrieved</li>
     *   <li>400 Bad Request - invalid query parameters</li>
     * </ul>
     *
     * @return paginated list of orders
     */
    @GetMapping
    public ResponseEntity<PageResponseDTO<OrderDTO>> getCurrentUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false, defaultValue = "timestamp,desc") String sort
    ) {
        Page<Order> orderPage = orderService.getCurrentUserOrders(
                status,
                page,
                limit,
                sort
        );

        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(orderMapper::toDto)
                .toList();

        PageResponseDTO<OrderDTO> response = new PageResponseDTO<>(
                orderDTOs,
                page,
                limit,
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a single order by its order number.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - order successfully retrieved</li>
     *   <li>404 Not Found - order does not exist</li>
     * </ul>
     *
     * @param orderNumber unique order identifier
     * @return the requested order
     */
    @GetMapping("/{orderNumber}")
    public OrderDTO getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return orderMapper.toDto(order);
    }

    /**
     * Cancels an existing order.
     *
     * <p>An order can only be cancelled if it is in a cancellable state.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - order successfully cancelled</li>
     *   <li>400 Bad Request - order cannot be cancelled in its current state</li>
     *   <li>404 Not Found - order does not exist</li>
     * </ul>
     *
     * @param orderNumber unique order identifier
     * @return the updated order
     */
    @PostMapping("/{orderNumber}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public OrderDTO cancelOrder(@PathVariable String orderNumber) {
        return orderMapper.toDto(
                orderService.cancelOrder(orderNumber)
        );
    }

    /**
     * Creates a new order from the current cart.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>201 Created - order successfully created</li>
     *   <li>400 Bad Request - cart is empty or invalid</li>
     * </ul>
     *
     * @param dto order creation data
     * @return the newly created order
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody OrderCreateDTO dto) {
        Order order = orderService.createOrder(dto);
        return orderMapper.toDto(order);
    }
}

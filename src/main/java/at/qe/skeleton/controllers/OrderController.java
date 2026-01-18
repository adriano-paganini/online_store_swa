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
     * Get orders of the currently authenticated user.
     */
    @GetMapping
    public ResponseEntity<PageResponseDTO<OrderDTO>> getCurrentUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) OrderStatus status
    ) {
        Page<Order> orderPage = orderService.getCurrentUserOrders(
                status,
                page,
                limit
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
     * Get a single order by order number.
     */
    @GetMapping("/{orderNumber}")
    public OrderDTO getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return orderMapper.toDto(order);
    }

    @PatchMapping("/{orderNumber}/confirm")
    public OrderDTO confirmOrder(@PathVariable String orderNumber) {
        Order order = orderService.confirmPayment(orderNumber);
        return orderMapper.toDto(order);
    }

    @PostMapping("/{orderNumber}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public OrderDTO cancelOrder(@PathVariable String orderNumber) {
        return orderMapper.toDto(
                orderService.cancelOrder(orderNumber)
        );
    }

    /**
     * Create a new order from the current cart.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody OrderCreateDTO dto) {
        Order order = orderService.createOrder(dto);
        return orderMapper.toDto(order);
    }
}

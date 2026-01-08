package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.OrderCreateDTO;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.OrderRepository;
import at.qe.skeleton.services.AuthenticatedUserService;
import at.qe.skeleton.services.CartService;
import at.qe.skeleton.services.OrderService;
import at.qe.skeleton.services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class OrderServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    private static final Long PRODUCT_ID = 10L;
    private static final String PRODUCT_NAME = "Test Product";
    private static final Integer  PRODUCT_QUANTITY = 2;
    private static final Double PRODUCT_PRICE = 50.0;
    private static final Double PRODUCT_DISCOUNT = 5.0;


    private static final Long BILLING_ADDRESS_ID = 100L;
    private static final Long SHIPPING_ADDRESS_ID = 101L;

    private static final String COUNTRY = "Austria";
    private static final String CITY_INNSBRUCK = "Innsbruck";
    private static final String CITY_GRAZ = "Graz";
    private static final String POSTAL = "6020";
    private static final String STREET = "Technikerstrasse";
    private static final String NUMBER = "1";
    private static final String EXTRA = "RR15";

    private static final String ORDER_NUMBER = "ORD-123";

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    private Userx user;
    private Userx otherUser;

    private Cart cart;
    private CartItem cartItem;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new Userx();
        user.setId(USER_ID);
        user.setAddresses(new ArrayList<>());

        otherUser = new Userx();
        otherUser.setId(OTHER_USER_ID);

        Address billingAddress = new Address();
        billingAddress.setId(BILLING_ADDRESS_ID);
        billingAddress.setCountry(COUNTRY);
        billingAddress.setCity(CITY_INNSBRUCK);
        billingAddress.setPostalCode(POSTAL);
        billingAddress.setStreet(STREET);
        billingAddress.setNumber(NUMBER);
        billingAddress.setExtra(EXTRA);

        Address shippingAddress = new Address();
        shippingAddress.setId(SHIPPING_ADDRESS_ID);
        shippingAddress.setCountry(COUNTRY);
        shippingAddress.setCity(CITY_GRAZ);
        shippingAddress.setPostalCode(POSTAL);
        shippingAddress.setStreet(STREET);
        shippingAddress.setNumber(NUMBER);
        shippingAddress.setExtra(null);

        user.getAddresses().addAll(List.of(billingAddress, shippingAddress));

        cartItem = new CartItem();
        cartItem.setProductId(PRODUCT_ID);
        cartItem.setQuantity(PRODUCT_QUANTITY);
        cartItem.setCurrentPrice(PRODUCT_PRICE);
        cartItem.setAppliedDiscount(PRODUCT_DISCOUNT);

        cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);

        Mockito.when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);
    }

    @Test
    void createOrderSuccess() {
        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(productService.getProductById(PRODUCT_ID))
                .thenReturn(Optional.of(product));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        Mockito.when(orderRepository.save(orderCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order saved = orderService.createOrder(dto);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(user, saved.getUser());
        Assertions.assertEquals(OrderStatus.PENDING, saved.getStatus());

        Assertions.assertNotNull(saved.getItems());
        Assertions.assertEquals(1, saved.getItems().size());

        OrderItem item = saved.getItems().getFirst();
        Assertions.assertEquals(PRODUCT_ID, item.getProductId());
        Assertions.assertEquals(PRODUCT_NAME, item.getProductName());
        Assertions.assertEquals(PRODUCT_PRICE, item.getPriceAtPurchase(), 0.001);
        Assertions.assertEquals(PRODUCT_DISCOUNT, item.getAppliedDiscount(), 0.001);
        Assertions.assertEquals(PRODUCT_QUANTITY, item.getQuantity());

        // total = (50 - 5) * 2 = 90
        double TOTAL = (PRODUCT_PRICE - PRODUCT_DISCOUNT) * PRODUCT_QUANTITY;
        Assertions.assertEquals(TOTAL, saved.getTotal(), 0.001);

        OrderAddress savedBilling = saved.getBillingAddress();
        Assertions.assertNotNull(savedBilling);
        Assertions.assertEquals(CITY_INNSBRUCK, savedBilling.getCity());
        Assertions.assertEquals(EXTRA, savedBilling.getExtra());

        OrderAddress savedShipping = saved.getShippingAddress();
        Assertions.assertNotNull(savedShipping);
        Assertions.assertEquals(CITY_GRAZ, savedShipping.getCity());
        Assertions.assertNull(savedShipping.getExtra());

        // verify save called with consistent entity too
        Order persistedArg = orderCaptor.getValue();
        Assertions.assertEquals(saved.getTotal(), persistedArg.getTotal(), 0.001);

        Mockito.verify(cartService).clearCart();
    }

    @Test
    void createOrderUnauthenticatedFails() {
        Mockito.when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(null);

        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(dto)
        );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).clearCart();
    }

    @Test
    void createOrderEmptyCartFails() {
        cart.setItems(new ArrayList<>());

        Mockito.when(cartService.getCart()).thenReturn(cart);

        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(dto)
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).clearCart();
    }

    @Test
    void createOrderBillingAddressNotOwnedFails() {
        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                999L
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(dto)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).clearCart();
    }

    @Test
    void createOrderShippingAddressNotOwnedFails() {
        OrderCreateDTO dto = new OrderCreateDTO(
                999L,
                BILLING_ADDRESS_ID
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(dto)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).clearCart();
    }

    @Test
    void createOrderProductNotFoundFails() {
        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(productService.getProductById(PRODUCT_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(dto)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).clearCart();
    }

    @Test
    void createOrderNullDiscountIsTreatedAsZero() {
        cartItem.setAppliedDiscount(null); // important edge case

        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(productService.getProductById(PRODUCT_ID))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order saved = orderService.createOrder(dto);

        // total = (50 - 0) * 2 = 100
        double TOTAL = (PRODUCT_PRICE - 0) * PRODUCT_QUANTITY;
        Assertions.assertEquals(TOTAL, saved.getTotal(), 0.001);
        Assertions.assertNull(saved.getItems().getFirst().getAppliedDiscount());
    }

    @Test
    void getOrderByNumberSuccess() {
        Order order = new Order(
                user,
                List.of(),
                new OrderAddress(COUNTRY, CITY_INNSBRUCK, POSTAL, STREET, NUMBER, EXTRA),
                new OrderAddress(COUNTRY, CITY_GRAZ, POSTAL, STREET, NUMBER, null),
                0.0
        );
        order.setOrderNumber(ORDER_NUMBER);

        Mockito.when(orderRepository.findByOrderNumber(ORDER_NUMBER))
                .thenReturn(Optional.of(order));

        Order result = orderService.getOrderByNumber(ORDER_NUMBER);

        Assertions.assertEquals(order, result);
    }

    @Test
    void getOrderByNumberNotFoundFails() {
        Mockito.when(orderRepository.findByOrderNumber(ORDER_NUMBER))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.getOrderByNumber(ORDER_NUMBER)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getOrderByNumberOfOtherUserFails() {
        Order order = new Order(
                otherUser,
                List.of(),
                new OrderAddress(COUNTRY, CITY_INNSBRUCK, POSTAL, STREET, NUMBER, EXTRA),
                new OrderAddress(COUNTRY, CITY_GRAZ, POSTAL, STREET, NUMBER, null),
                0.0
        );
        order.setOrderNumber(ORDER_NUMBER);

        Mockito.when(orderRepository.findByOrderNumber(ORDER_NUMBER))
                .thenReturn(Optional.of(order));

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.getOrderByNumber(ORDER_NUMBER)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getOrderByNumberUnauthenticatedFails() {
        Mockito.when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(null);

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.getOrderByNumber(ORDER_NUMBER)
        );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void getCurrentUserOrdersWithoutStatusCallsFindByUser() {
        Page<Order> page = new PageImpl<>(List.of(new Order(
                user,
                List.of(),
                new OrderAddress(COUNTRY, CITY_INNSBRUCK, POSTAL, STREET, NUMBER, EXTRA),
                new OrderAddress(COUNTRY, CITY_GRAZ, POSTAL, STREET, NUMBER, null),
                0.0
        )));

        Mockito.when(orderRepository.findByUser(Mockito.eq(user), Mockito.any(Pageable.class)))
                .thenReturn(page);

        Page<Order> result = orderService.getCurrentUserOrders(null, 0, 10);

        Assertions.assertEquals(1, result.getTotalElements());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(orderRepository).findByUser(Mockito.eq(user), pageableCaptor.capture());

        Pageable used = pageableCaptor.getValue();
        Assertions.assertEquals(0, used.getPageNumber());
        Assertions.assertEquals(10, used.getPageSize());
        Assertions.assertTrue(used.getSort().getOrderFor("timestamp").isDescending());
    }

    @Test
    void getCurrentUserOrdersWithStatusCallsFindByUserAndStatus() {
        Page<Order> page = new PageImpl<>(List.of(new Order(
                user,
                List.of(),
                new OrderAddress(COUNTRY, CITY_INNSBRUCK, POSTAL, STREET, NUMBER, EXTRA),
                new OrderAddress(COUNTRY, CITY_GRAZ, POSTAL, STREET, NUMBER, null),
                0.0
        )));

        Mockito.when(orderRepository.findByUserAndStatus(
                        Mockito.eq(user),
                        Mockito.eq(OrderStatus.PENDING),
                        Mockito.any(Pageable.class)))
                .thenReturn(page);

        Page<Order> result = orderService.getCurrentUserOrders(OrderStatus.PENDING, 1, 5);

        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(orderRepository).findByUserAndStatus(
                Mockito.eq(user),
                Mockito.eq(OrderStatus.PENDING),
                Mockito.any(Pageable.class)
        );
    }

    @Test
    void getCurrentUserOrdersUnauthenticatedFails() {
        Mockito.when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(null);

        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> orderService.getCurrentUserOrders(null, 0, 10)
        );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void orderAddressIsFrozenAfterCreation() {
        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(productService.getProductById(PRODUCT_ID))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(dto);

        // mutate original address after order creation
        Address billing = user.getAddresses().stream()
                .filter(a -> a.getId().equals(BILLING_ADDRESS_ID))
                .findFirst()
                .orElseThrow();

        billing.setCity("Vienna");
        billing.setStreet("Changed Street");

        OrderAddress snapshot = order.getBillingAddress();
        Assertions.assertEquals(CITY_INNSBRUCK, snapshot.getCity());
        Assertions.assertEquals(STREET, snapshot.getStreet());
    }

    @Test
    void productNameIsFrozenAtPurchaseTime() {
        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(productService.getProductById(PRODUCT_ID))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(dto);

        // change product after order creation
        product.setName("Renamed Product");

        OrderItem item = order.getItems().getFirst();
        Assertions.assertEquals(PRODUCT_NAME, item.getProductName());
    }

    @Test
    void productPriceIsFrozenAtPurchaseTime() {
        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(productService.getProductById(PRODUCT_ID))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(dto);

        // change price after order creation
        cartItem.setCurrentPrice(999.0);

        OrderItem item = order.getItems().getFirst();
        Assertions.assertEquals(PRODUCT_PRICE, item.getPriceAtPurchase(), 0.001);
    }

    @Test
    void orderTotalIsFrozenAfterCreation() {
        OrderCreateDTO dto = new OrderCreateDTO(
                SHIPPING_ADDRESS_ID,
                BILLING_ADDRESS_ID
        );

        Mockito.when(cartService.getCart()).thenReturn(cart);
        Mockito.when(productService.getProductById(PRODUCT_ID))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(dto);

        // change cart after order creation
        cartItem.setQuantity(99);
        cartItem.setAppliedDiscount(99.0);

        double expected = (PRODUCT_PRICE - PRODUCT_DISCOUNT) * PRODUCT_QUANTITY;
        Assertions.assertEquals(expected, order.getTotal(), 0.001);
    }
}

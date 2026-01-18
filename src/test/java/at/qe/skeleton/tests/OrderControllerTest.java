package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.configs.TokenAuthenticationFilter;
import at.qe.skeleton.controllers.OrderController;
import at.qe.skeleton.dtos.*;
import at.qe.skeleton.mappers.OrderMapper;
import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.model.ShippingMethod;
import at.qe.skeleton.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    private static final String ENDPOINT = "/orders";
    private static final String ORDER_NUMBER = "ORD-ABC123";

    private static final String ORDER_STATUS = "PENDING";

    private static final Long ADDRESS_SHIPPING = 1L;
    private static final Long ADDRESS_BILLING = 2L;

    private static final Double TOTAL = 90.0;

    private static final Integer PAGE_DEFAULT = 0;
    private static final Integer LIMIT_DEFAULT = 10;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoSpyBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @MockitoBean
    private at.qe.skeleton.services.UserxService userxService;

    @MockitoBean
    private JwtConfig jwtConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() throws Exception {
        order = new Order(
                null,
                List.of(),
                null,
                null,
                ShippingMethod.FAIRY_DUST_DISPATCH,
                TOTAL
        );

        orderDTO = new OrderDTO(
                ORDER_NUMBER,
                OrderStatus.PENDING,
                TOTAL,
                LocalDateTime.now(),
                List.of(),
                null,
                null,
                ShippingMethod.FAIRY_DUST_DISPATCH
        );

        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(tokenAuthenticationFilter).doFilterInternal(
                Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class),
                Mockito.any(FilterChain.class)
        );

        @SuppressWarnings("unchecked")
        Jws<Claims> mockJws = (Jws<Claims>) Mockito.mock(Jws.class);
        Claims mockClaims = Mockito.mock(Claims.class);
        Mockito.when(mockClaims.getSubject()).thenReturn("testuser");
        Mockito.when(mockJws.getPayload()).thenReturn(mockClaims);

        Mockito.when(jwtTokenProvider.validateTokenAndGetJws(Mockito.anyString()))
                .thenReturn(java.util.Optional.of(mockJws));
    }

    @Test
    @WithMockUser
    void getCurrentUserOrdersSuccess() throws Exception {
        Page<Order> page = new PageImpl<>(List.of(order));

        Mockito.when(orderService.getCurrentUserOrders(
                null, PAGE_DEFAULT, LIMIT_DEFAULT
        )).thenReturn(page);

        Mockito.when(orderMapper.toDto(order))
                .thenReturn(orderDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderNumber")
                        .value(ORDER_NUMBER))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements")
                        .value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page")
                        .value(PAGE_DEFAULT))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limit")
                        .value(LIMIT_DEFAULT));
    }

    @Test
    @WithMockUser
    void getCurrentUserOrdersWithStatus() throws Exception {
        Page<Order> page = new PageImpl<>(List.of(order));

        Mockito.when(orderService.getCurrentUserOrders(
                OrderStatus.PENDING, PAGE_DEFAULT, LIMIT_DEFAULT
        )).thenReturn(page);

        Mockito.when(orderMapper.toDto(order))
                .thenReturn(orderDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT)
                        .param("status", ORDER_STATUS))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].status")
                        .value(ORDER_STATUS));
    }

    @Test
    void getCurrentUserOrdersUnauthenticatedFails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getOrderByNumberSuccess() throws Exception {
        Mockito.when(orderService.getOrderByNumber(ORDER_NUMBER))
                .thenReturn(order);

        Mockito.when(orderMapper.toDto(order))
                .thenReturn(orderDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/{orderNumber}", ORDER_NUMBER))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderNumber")
                        .value(ORDER_NUMBER))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(ORDER_STATUS));
    }

    @Test
    @WithMockUser
    void getOrderByNumberNotFoundFails() throws Exception {
        Mockito.when(orderService.getOrderByNumber(ORDER_NUMBER))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/{orderNumber}", ORDER_NUMBER))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    void createOrderSuccess() throws Exception {
        OrderCreateDTO createDTO = new OrderCreateDTO(
                ADDRESS_SHIPPING,
                ADDRESS_BILLING,
                ShippingMethod.FAIRY_DUST_DISPATCH
        );

        Mockito.when(orderService.createOrder(Mockito.any()))
                .thenReturn(order);

        Mockito.when(orderMapper.toDto(order))
                .thenReturn(orderDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderNumber")
                        .value(ORDER_NUMBER))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total")
                        .value(TOTAL));
    }

    @Test
    void createOrderUnauthenticatedFails() throws Exception {
        OrderCreateDTO createDTO = new OrderCreateDTO(ADDRESS_SHIPPING, ADDRESS_BILLING, ShippingMethod.FAIRY_DUST_DISPATCH);

        mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void cancelOrderSuccess() throws Exception {
        Mockito.when(orderService.cancelOrder(ORDER_NUMBER))
                .thenReturn(order);

        Mockito.when(orderMapper.toDto(order))
                .thenReturn(orderDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(ENDPOINT + "/{orderNumber}/cancel", ORDER_NUMBER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderNumber")
                        .value(ORDER_NUMBER));
    }

    @Test
    void cancelOrderUnauthenticatedFails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(ENDPOINT + "/{orderNumber}/cancel", ORDER_NUMBER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createOrderSuccess_returnsExactDtoJson() throws Exception {
        OrderCreateDTO createDTO = new OrderCreateDTO(
                ADDRESS_SHIPPING,
                ADDRESS_BILLING,
                ShippingMethod.FAIRY_DUST_DISPATCH
        );

        Mockito.when(orderService.createOrder(Mockito.any()))
                .thenReturn(order);

        Mockito.when(orderMapper.toDto(order))
                .thenReturn(orderDTO);

        String expectedJson = objectMapper.writeValueAsString(orderDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }
}

package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.configs.TokenAuthenticationFilter;
import at.qe.skeleton.controllers.PaymentController;
import at.qe.skeleton.dtos.PaymentRequestDTO;
import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderStatus;
import at.qe.skeleton.services.OrderService;
import at.qe.skeleton.services.UserxService;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Optional;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoSpyBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @MockitoBean
    private JwtConfig jwtConfig;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserxService userService;

    @Autowired
    private ObjectMapper objectMapper;

    static final String ORDER_NUMBER = "ORD-123";

    @BeforeEach
    void setUp() throws Exception {
        Order order = Mockito.mock(Order.class);
        Mockito.when(order.getStatus()).thenReturn(OrderStatus.PENDING);
        Mockito.when(order.getTotal()).thenReturn(99.99);

        Mockito.when(orderService.getOrderByNumber(ORDER_NUMBER)).thenReturn(order);

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
        Mockito.when(mockClaims.getSubject()).thenReturn("user1");
        Mockito.when(mockJws.getPayload()).thenReturn(mockClaims);
        Mockito.when(jwtTokenProvider.validateTokenAndGetJws(Mockito.anyString()))
                .thenReturn(Optional.of(mockJws));
    }

    @Test
    @WithMockUser(username = "user1")
    void processPaymentSuccess() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                99.99,
                ORDER_NUMBER,
                "credit_card",
                "4111-1111-1111-1111",
                "John Doe",
                "12/25",
                "123"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/payment")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Payment processed successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists());

        Mockito.verify(orderService).confirmPayment(
                Mockito.eq(ORDER_NUMBER),
                Mockito.anyString()
        );

    }

    @Test
    @WithMockUser(username = "user1")
    void processPaymentInvalidAmount() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                0.0,
                ORDER_NUMBER,
                "credit_card",
                "4111-1111-1111-1111",
                "John Doe",
                "12/25",
                "123"
        );

        
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/payment")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(orderService, Mockito.never())
                .confirmPayment(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    @WithMockUser(username = "user1")
    void processPaymentDeclinedCard() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                99.99,
                ORDER_NUMBER,
                "credit_card",
                "0000-0000-0000-0000",
                "John Doe",
                "12/25",
                "123"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/payment")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Payment declined: Invalid card"));
    }

    @Test
    @WithMockUser(username = "user1")
    void processPaymentMissingRequiredFields() throws Exception {
        
        String invalidJson = "{\"cardNumber\":\"4111-1111-1111-1111\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/payment")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void processPaymentUnauthenticated() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                99.99,
                ORDER_NUMBER,
                "credit_card",
                "4111-1111-1111-1111",
                "John Doe",
                "12/25",
                "123"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/payment")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}

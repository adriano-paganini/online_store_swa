package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.configs.TokenAuthenticationFilter;
import at.qe.skeleton.controllers.SubscriptionController;
import at.qe.skeleton.dtos.SubscriptionCreateDTO;
import at.qe.skeleton.dtos.SubscriptionDTO;
import at.qe.skeleton.dtos.SubscriptionUpdateDTO;
import at.qe.skeleton.mappers.SubscriptionMapper;
import at.qe.skeleton.mappers.SubscriptionCreateMapper;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.SubscriptionType;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.SubscriptionService;
import at.qe.skeleton.services.UserxService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(SubscriptionController.class)
@AutoConfigureMockMvc
public class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @MockitoBean
    private SubscriptionMapper subscriptionMapper;

    @MockitoBean
    private SubscriptionCreateMapper subscriptionCreateMapper;

    @MockitoBean
    private UserxService userxService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JwtConfig jwtConfig;
    @MockitoSpyBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);

            Userx mockUser = new Userx();
            mockUser.setId(1L);
            mockUser.setUsername("testuser");

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    mockUser, null, java.util.Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(request, response);
            return null;
        }).when(tokenAuthenticationFilter).doFilterInternal(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSubscription() throws Exception {
        SubscriptionCreateDTO createDTO = new SubscriptionCreateDTO(
                10L,
                Set.of(SubscriptionType.RESTOCK),
                Set.of(NotificationType.EMAIL)
        );

        Subscription mockSub = new Subscription();
        SubscriptionDTO responseDTO = new SubscriptionDTO(1L, 1L, 10L, Set.of(SubscriptionType.RESTOCK), Set.of(NotificationType.EMAIL));

        Mockito.when(subscriptionService.createSubscription(any(), any(Subscription.class)))
                .thenReturn(mockSub);
        Mockito.when(subscriptionMapper.mapTo(mockSub)).thenReturn(responseDTO);
        Mockito.when(subscriptionCreateMapper.mapFrom(createDTO)).thenReturn(mockSub);

        mockMvc.perform(MockMvcRequestBuilders.post("/subscriptions")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSubscription() throws Exception {
        SubscriptionUpdateDTO updateDTO = new SubscriptionUpdateDTO(
                Set.of(SubscriptionType.PRICEUPDATE),
                Set.of(NotificationType.SMS)
        );

        Subscription mockSub = new Subscription();
        SubscriptionDTO responseDTO = new SubscriptionDTO(100L, 1L, 10L, Set.of(SubscriptionType.PRICEUPDATE), Set.of(NotificationType.SMS));

        Mockito.when(subscriptionService.updateSubscription(Mockito.eq(100L), any(SubscriptionUpdateDTO.class)))
                .thenReturn(mockSub);
        Mockito.when(subscriptionMapper.mapTo(mockSub)).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch("/subscriptions/{id}", 100L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSubscription() throws Exception {
        Long subscriptionId = 100L;
        Mockito.doNothing().when(subscriptionService).deleteSubscription(subscriptionId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/subscriptions/{id}", subscriptionId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(subscriptionService).deleteSubscription(subscriptionId);
    }

    @Test
    void getSubscriptionsUnauthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/subscriptions"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    @Test
    @WithMockUser(username = "testuser")
    void getSubscriptionByProductIdSuccess() throws Exception {
        Long productId = 10L;
        Subscription mockSub = new Subscription();
        SubscriptionDTO responseDTO = new SubscriptionDTO(1L, 1L, productId, Set.of(SubscriptionType.RESTOCK), Set.of(NotificationType.EMAIL));

        Mockito.when(subscriptionService.getSubscriptionByUserAndProduct(any(), Mockito.eq(productId)))
                .thenReturn(java.util.Optional.of(mockSub));

        Mockito.when(subscriptionMapper.mapTo(mockSub)).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/subscriptions/product/{id}", productId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSubscriptionByProductIdNotFound() throws Exception {
        Long productId = 999L;

        Mockito.when(subscriptionService.getSubscriptionByUserAndProduct(any(), Mockito.eq(productId)))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/subscriptions/product/{id}", productId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserSubscriptionsSuccess() throws Exception {
        Subscription mockSub = new Subscription();
        SubscriptionDTO responseDTO = new SubscriptionDTO(1L, 1L, 10L, Set.of(SubscriptionType.RESTOCK), Set.of(NotificationType.EMAIL));

        org.springframework.data.domain.Page<Subscription> page = new org.springframework.data.domain.PageImpl<>(java.util.List.of(mockSub));

        Mockito.when(subscriptionService.getUserSubscriptions(
                        any(), Mockito.anyInt(), Mockito.anyInt(), any(), any(), Mockito.anyString()))
                .thenReturn(page);

        Mockito.when(subscriptionMapper.mapTo(mockSub)).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/subscriptions")
                        .param("page", "0")
                        .param("limit", "6"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limit").value(6));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserSubscriptionsInternalError() throws Exception {
        Mockito.when(subscriptionService.getUserSubscriptions(
                        any(), Mockito.anyInt(), Mockito.anyInt(), any(), any(), Mockito.anyString()))
                .thenThrow(new RuntimeException("Database down"));

        mockMvc.perform(MockMvcRequestBuilders.get("/subscriptions"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.status().reason(org.hamcrest.Matchers.containsString("Error fetching Subscriptions")));
    }
}
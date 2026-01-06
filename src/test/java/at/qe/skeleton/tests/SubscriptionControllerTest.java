package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.controllers.SubscriptionController;
import at.qe.skeleton.dtos.SubscriptionCreateDTO;
import at.qe.skeleton.dtos.SubscriptionDTO;
import at.qe.skeleton.dtos.SubscriptionUpdateDTO;
import at.qe.skeleton.mappers.SubscriptionMapper;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.SubscriptionType;
import at.qe.skeleton.services.SubscriptionService;
import at.qe.skeleton.services.UserxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

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
    private UserxService userxService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JwtConfig jwtConfig;

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

        Mockito.when(subscriptionService.createSubscription(Mockito.any(), Mockito.any(SubscriptionCreateDTO.class)))
                .thenReturn(mockSub);
        Mockito.when(subscriptionMapper.mapTo(mockSub)).thenReturn(responseDTO);

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

        Mockito.when(subscriptionService.updateSubscription(Mockito.eq(100L), Mockito.any(SubscriptionUpdateDTO.class)))
                .thenReturn(mockSub);
        Mockito.when(subscriptionMapper.mapTo(mockSub)).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/subscriptions/{id}", 100L)
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
}
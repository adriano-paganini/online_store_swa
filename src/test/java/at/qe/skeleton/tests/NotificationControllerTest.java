package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.controllers.NotificationController;
import at.qe.skeleton.dtos.NotificationResponseDTO;
import at.qe.skeleton.mappers.NotificationResponseMapper;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.NotificationService;
import at.qe.skeleton.services.UserxService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private NotificationResponseMapper notificationResponseMapper;

    @MockitoBean
    private UserxService userService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JwtConfig jwtConfig;

    private Userx testUser;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetUserNotificationsSuccess() throws Exception {
        int page = 0;
        int limit = 6;
        String sort = "timestamp,desc";

        Notification notification = new Notification(1L, "Price Change Product A", NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.SENT);
        notification.setTimestamp(LocalDateTime.now());

        Page<Notification> notificationPage = new PageImpl<>(List.of(notification));

        NotificationResponseDTO responseDTO = new NotificationResponseDTO(
                "Price Change Product A",
                NotificationType.EMAIL,
                NotificationStatus.SENT,
                LocalDateTime.now()
        );

        Mockito.when(notificationService.getUserNotifications(
                        Mockito.any(),
                        Mockito.eq(page),
                        Mockito.eq(limit),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.eq(sort)))
                .thenReturn(notificationPage);

        Mockito.when(notificationResponseMapper.mapTo(notification)).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications")
                        .param("page", String.valueOf(page))
                        .param("limit", String.valueOf(limit))
                        .param("sort", sort)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].message").value("Price Change Product A"));
    }

    @Test
    void testGetUserNotificationsUnauthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/notifications"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetUserNotificationsWithFilters() throws Exception {
        NotificationStatus filterStatus = NotificationStatus.SENT;
        NotificationType filterChannel = NotificationType.SMS;

        Mockito.when(notificationService.getUserNotifications(
                        Mockito.any(),
                        Mockito.anyInt(),
                        Mockito.anyInt(),
                        Mockito.eq(filterStatus),
                        Mockito.eq(filterChannel),
                        Mockito.anyString()))
                .thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications")
                        .param("status", filterStatus.name())
                        .param("channel", filterChannel.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(notificationService).getUserNotifications(
                Mockito.any(),
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.eq(filterStatus),
                Mockito.eq(filterChannel),
                Mockito.anyString());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetUserNotificationsServerError() throws Exception {
        Mockito.when(notificationService.getUserNotifications(
                        Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
                        Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenThrow(new RuntimeException("Database down"));

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
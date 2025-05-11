package TGJavaProjects.TasksApplication.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(controllers = NotificationController.class)
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notification1;
    private Notification notification2;
    private Notification notification1Read;

    private static final long USER_ID_1 = 1L;
    private static final long NOTIFICATION_ID_1 = 10L;
    private static final long NOTIFICATION_ID_2 = 20L;
    private static final long TASK_ID_FOR_NOTIFICATION_1 = 100L;
    private static final long TASK_ID_FOR_NOTIFICATION_2 = 101L;
    private static final long NON_EXISTENT_USER_ID = 99L;
    private static final long NON_EXISTENT_NOTIFICATION_ID = 999L;
    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        notification1 = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .userId(USER_ID_1)
                .taskId(TASK_ID_FOR_NOTIFICATION_1)
                .text("Notification 1 text")
                .date(NOW.minusHours(1))
                .isRead(false)
                .build();

        notification2 = Notification.builder()
                .notificationId(NOTIFICATION_ID_2)
                .userId(USER_ID_1)
                .taskId(TASK_ID_FOR_NOTIFICATION_2)
                .text("Notification 2 text")
                .date(NOW)
                .isRead(false)
                .build();

        notification1Read = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .userId(USER_ID_1)
                .taskId(TASK_ID_FOR_NOTIFICATION_1)
                .text("Notification 1 text")
                .date(NOW.minusHours(1))
                .isRead(true)
                .build();
    }

    // getAllUserNotifications

    @Test
    void getAllUserNotifications_ReturnsListOfNotifications_WhenUserExists() throws Exception {
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationService.getAllNotificationsByUserId(USER_ID_1)).thenReturn(notifications);

        mockMvc.perform(get("/api/v1/users/{userId}/notifications", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].notificationId", is((int) NOTIFICATION_ID_1)))
                .andExpect(jsonPath("$[1].notificationId", is((int) NOTIFICATION_ID_2)));

        verify(notificationService, times(1)).getAllNotificationsByUserId(USER_ID_1);
    }

    @Test
    void getAllUserNotifications_ReturnsEmptyList_WhenUserHasNoNotifications() throws Exception {
        when(notificationService.getAllNotificationsByUserId(USER_ID_1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/{userId}/notifications", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(notificationService, times(1)).getAllNotificationsByUserId(USER_ID_1);
    }

    @Test
    void getAllUserNotifications_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(notificationService.getAllNotificationsByUserId(NON_EXISTENT_USER_ID))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{userId}/notifications", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(notificationService, times(1)).getAllNotificationsByUserId(NON_EXISTENT_USER_ID);
    }

    // getPendingUserNotifications

    @Test
    void getPendingUserNotifications_ReturnsListOfPendingNotifications() throws Exception {
        List<Notification> pendingNotifications = Arrays.asList(notification1, notification2);
        when(notificationService.getPendingNotificationsByUserId(USER_ID_1)).thenReturn(pendingNotifications);

        mockMvc.perform(get("/api/v1/users/{userId}/notifications/pending", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].notificationId", is((int) NOTIFICATION_ID_1)))
                .andExpect(jsonPath("$[0].isRead", is(false)))
                .andExpect(jsonPath("$[1].notificationId", is((int) NOTIFICATION_ID_2)))
                .andExpect(jsonPath("$[1].isRead", is(false)));
        verify(notificationService, times(1)).getPendingNotificationsByUserId(USER_ID_1);
    }

    @Test
    void getPendingUserNotifications_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(notificationService.getPendingNotificationsByUserId(NON_EXISTENT_USER_ID))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{userId}/notifications/pending", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(notificationService, times(1))
                .getPendingNotificationsByUserId(NON_EXISTENT_USER_ID);
    }

    // markNotificationAsRead

    @Test
    void markNotificationAsRead_ReturnsNoContent_WhenSuccessful() throws Exception {
        doNothing().when(notificationService).markNotificationAsRead(USER_ID_1, NOTIFICATION_ID_1);

        mockMvc.perform(put("/api/v1/users/{userId}/notifications/{notificationId}/read",
                        USER_ID_1, NOTIFICATION_ID_1))
                .andExpect(status().isNoContent());
        verify(notificationService, times(1)).markNotificationAsRead(USER_ID_1,
                NOTIFICATION_ID_1);
    }

    @Test
    void markNotificationAsRead_ReturnsNotFound_WhenNotificationDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Notification not found"))
                .when(notificationService).markNotificationAsRead(USER_ID_1, NON_EXISTENT_NOTIFICATION_ID);

        mockMvc.perform(put("/api/v1/users/{userId}/notifications/{notificationId}/read",
                        USER_ID_1, NON_EXISTENT_NOTIFICATION_ID))
                .andExpect(status().isNotFound());
        verify(notificationService, times(1)).markNotificationAsRead(USER_ID_1,
                NON_EXISTENT_NOTIFICATION_ID);
    }

    @Test
    void markNotificationAsRead_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(notificationService).markNotificationAsRead(NON_EXISTENT_USER_ID, NOTIFICATION_ID_1);

        mockMvc.perform(put("/api/v1/users/{userId}/notifications/{notificationId}/read",
                        NON_EXISTENT_USER_ID, NOTIFICATION_ID_1))
                .andExpect(status().isNotFound());
        verify(notificationService, times(1))
                .markNotificationAsRead(NON_EXISTENT_USER_ID, NOTIFICATION_ID_1);
    }
}
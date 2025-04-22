package TGJavaProjects.TasksApplication.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
@AutoConfigureMockMvc(addFilters = false)
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
    private static final long TASK_ID_1 = 1L;
    private static final long NOTIFICATION_ID_1 = 10L;
    private static final long NOTIFICATION_ID_2 = 33L;
    private static final long NON_EXISTENT_TASK_ID = 14L;
    private static final long USER_ID_1 = 1L;
    private static final long NON_EXISTENT_USER_ID = 88L;

    @BeforeEach
    void setUp() {
        notification1 = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .taskId(TASK_ID_1)
                .text("Notification 1 Text")
                .date(LocalDateTime.now().minusHours(1))
                .build();

        notification2 = Notification.builder()
                .notificationId(NOTIFICATION_ID_2)
                .taskId(TASK_ID_1)
                .text("Notification 2 Text")
                .date(LocalDateTime.now())
                .build();
    }

    // getAllNotifications

    @Test
    void getAllNotifications_ReturnsListOfNotifications() throws Exception {
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationService.findAllNotifications()).thenReturn(notifications);

        mockMvc.perform(get("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].notificationId", is((int) NOTIFICATION_ID_1)))
                .andExpect(jsonPath("$[1].notificationId", is((int) NOTIFICATION_ID_2)));

        verify(notificationService, times(1)).findAllNotifications();
    }

    @Test
    void getAllNotifications_ReturnsEmptyList() throws Exception {
        when(notificationService.findAllNotifications()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(notificationService, times(1)).findAllNotifications();
    }

    // getUserNotifications

    @Test
    void getUserNotifications_ReturnsListOfNotifications() throws Exception {
        List<Notification> userNotifications = List.of(notification1);
        when(notificationService.findNotificationsByUserId(USER_ID_1)).thenReturn(userNotifications);

        mockMvc.perform(get("/api/v1/notifications/user/{userId}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].notificationId", is((int) NOTIFICATION_ID_1)));

        verify(notificationService, times(1)).findNotificationsByUserId(USER_ID_1);
    }

    @Test
    void getUserNotifications_ReturnsEmptyList_WhenUserHasNoNotifications() throws Exception {
        when(notificationService.findNotificationsByUserId(USER_ID_1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/notifications/user/{userId}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(notificationService, times(1)).findNotificationsByUserId(USER_ID_1);
    }

    @Test
    void getUserNotifications_ReturnsNotFound_WhenServiceThrowsNotFound() throws Exception {
        when(notificationService.findNotificationsByUserId(NON_EXISTENT_USER_ID))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/notifications/user/{userId}", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).findNotificationsByUserId(NON_EXISTENT_USER_ID);
    }

    // getTaskNotifications

    @Test
    void getTaskNotifications_ReturnsListOfNotifications_WhenTaskExists() throws Exception {
        List<Notification> taskNotifications = List.of(notification1);
        when(notificationService.findNotificationsByTaskId(TASK_ID_1)).thenReturn(taskNotifications);

        mockMvc.perform(get("/api/v1/notifications/task/{taskId}", TASK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].notificationId", is((int) NOTIFICATION_ID_1)))
                .andExpect(jsonPath("$[0].taskId", is((int) TASK_ID_1)));

        verify(notificationService, times(1)).findNotificationsByTaskId(TASK_ID_1);
    }

    @Test
    void getTaskNotifications_ReturnsEmptyList_WhenTaskHasNoNotifications() throws Exception {
        when(notificationService.findNotificationsByTaskId(TASK_ID_1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/notifications/task/{taskId}", TASK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(notificationService, times(1)).findNotificationsByTaskId(TASK_ID_1);
    }

    @Test
    void getTaskNotifications_ReturnsNotFound_WhenTaskDoesNotExist() throws Exception {
        when(notificationService.findNotificationsByTaskId(NON_EXISTENT_TASK_ID))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(get("/api/v1/notifications/task/{taskId}", NON_EXISTENT_TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).findNotificationsByTaskId(NON_EXISTENT_TASK_ID);
    }

}
package TGJavaProjects.TasksApplication.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.service.NotificationService;
import TGJavaProjects.TasksApplication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Мок в Spring-контексте
    private NotificationService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification("test notification", 1L, LocalDateTime.now(), 1L);
    }

    @Test
    void getAllNotifications_ReturnsListOfNotifications_WhenNotificationsExist() throws Exception {

        when(service.getAllNotifications()).thenReturn(Arrays.asList(notification));

        mockMvc.perform(get("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1));

        verify(service, times(1)).getAllNotifications();
    }

    @Test
    void getAllNotifications_ReturnsNotFound_WhenNotificationsDoNotExist() throws Exception {

        when(service.getAllNotifications()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getAllNotifications();
    }

    @Test
    void getUserNotifications_ReturnsListOfNotifications_WhenNotificationsExist() throws Exception {
        when(service.getUserNotifications(notification.getUserId()))
                .thenReturn(Arrays.asList(notification));

        mockMvc.perform(get("/api/v1/notifications/user/" + notification.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(service, times(1)).getUserNotifications(notification.getUserId());
    }

    @Test
    void getUserNotifications_ReturnsNotFound_WhenNotificationsDoNotExist() throws Exception {
        when(service.getUserNotifications(notification.getUserId()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notifications/user/" + notification.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getUserNotifications(notification.getUserId());
    }

    @Test
    void getTaskNotifications_ReturnsListOfNotifications_WhenNotificationsExist() throws Exception {
        when(service.getTaskNotifications(notification.getTaskId()))
                .thenReturn(Arrays.asList(notification));

        mockMvc.perform(get("/api/v1/notifications/task/" + notification.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(service, times(1)).getTaskNotifications(notification.getTaskId());
    }

    @Test
    void getTaskNotifications_ReturnsNotFound_WhenNotificationsDoNotExist() throws Exception {
        when(service.getTaskNotifications(notification.getTaskId()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notifications/task/" + notification.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getTaskNotifications(notification.getTaskId());
    }
}
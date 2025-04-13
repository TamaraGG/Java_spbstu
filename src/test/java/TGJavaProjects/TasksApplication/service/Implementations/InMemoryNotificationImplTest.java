package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.controller.NotificationController;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.InMemoryNotificationDAO;
import TGJavaProjects.TasksApplication.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Not;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryNotificationImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private InMemoryNotificationImpl notificationService;

    @Mock
    private InMemoryUserServiceImpl userService;
    @Mock
    private InMemoryTaskServiceImpl taskService;
    @Mock
    private InMemoryNotificationDAO notificationDAO;

    private Notification notification;
    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        notification = new Notification(
                "test notification", 1L,
                LocalDateTime.now(), 1L);
        user = new User(
                1L,
                "Jane",
                "Doe",
                "test@gmail.com"
        );
        task = new Task(
                1L, "test task",
                LocalDateTime.now(), LocalDateTime.now(), true, 1L
        );
    }

    @Test
    void getAllNotifications_ReturnsListOfNotifications_WhenNotEmpty() {

        when(notificationDAO.getAllNotifications())
                .thenReturn(List.of(notification));

        List<Notification> resNotifications = notificationService.getAllNotifications();

        assertEquals(1, resNotifications.size());
        verify(notificationDAO, times(1)).getAllNotifications();
    }

    @Test
    void getAllNotifications_ReturnsListOfNotifications_WhenEmpty() {

        when(notificationDAO.getAllNotifications())
                .thenReturn(List.of());

        List<Notification> resNotifications = notificationService.getAllNotifications();

        assertEquals(0, resNotifications.size());
        verify(notificationDAO, times(1)).getAllNotifications();
    }

    @Test
    void getUserNotifications_ReturnsListOfUserNotifications_WhenUserExists() {
        when(userService.findUserById(notification.getUserId()))
                .thenReturn(Optional.ofNullable(user));
        when(notificationDAO.getUserNotifications(notification.getUserId()))
                .thenReturn(List.of(notification));

        Optional<List<Notification>> result = notificationService.getUserNotifications(notification.getUserId());

        assert(result.isPresent());
        assertEquals(1, result.get().size());
        verify(notificationDAO, times(1)).getUserNotifications(notification.getUserId());
    }

    @Test
    void getUserNotifications_ReturnsListOfUserNotifications_WhenUserDoesNotExist() {
        when(userService.findUserById(notification.getUserId()))
                .thenReturn(Optional.empty());

        Optional<List<Notification>> result = notificationService.getUserNotifications(notification.getUserId());

        assert(result.isEmpty());
        verify(notificationDAO, times(0)).getUserNotifications(notification.getUserId());
    }

    @Test
    void getTaskNotifications_ReturnsListOfTaskNotifications_WhenTaskExists() {

        when(taskService.getTaskById(notification.getTaskId()))
                .thenReturn(Optional.of(task));
        when(notificationDAO.getTaskNotifications(notification.getTaskId()))
                .thenReturn(List.of(notification));

        Optional<List<Notification>> result = notificationService.getTaskNotifications(notification.getTaskId());

        assert(result.isPresent());
        assertEquals(1, result.get().size());
        verify(notificationDAO, times(1)).getTaskNotifications(notification.getTaskId());
    }

    @Test
    void getTaskNotifications_ReturnsListOfTaskNotifications_WhenTaskDoesNotExist() {
        when(taskService.getTaskById(notification.getTaskId()))
                .thenReturn(Optional.empty());

        Optional<List<Notification>> result = notificationService.getTaskNotifications(notification.getTaskId());

        assert(result.isEmpty());
        verify(notificationDAO, times(0)).getTaskNotifications(notification.getTaskId());
    }


}
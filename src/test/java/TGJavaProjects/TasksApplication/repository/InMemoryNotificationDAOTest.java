package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InMemoryNotificationDAOTest {

    @InjectMocks
    private InMemoryNotificationDAO repository;

    private static final Notification notification1 = mock(Notification.class);
    private static final Notification notification2 = mock(Notification.class);

    private static final long userId = 1L;
    private static final long taskId = 1L;


    @BeforeEach
    void setUp() {

    }

    @Test
    void getAllNotifications_ReturnsListOfNotifications_WhenNotEmpty() {
        repository.addNotification(notification1);
        repository.addNotification(notification2);

        List<Notification> receivedNotifications = repository.getAllNotifications();

        assertNotNull(receivedNotifications);
        assertEquals(2, receivedNotifications.size());
    }

    @Test
    void getAllNotifications_ReturnsListOfNotifications_WhenEmpty() {

        List<Notification> receivedNotifications = repository.getAllNotifications();

        assertNotNull(receivedNotifications);
        assertEquals(0, receivedNotifications.size());

    }

    @Test
    void getUserNotifications_ReturnsListOfUserNotifications_WhenNotEmpty() {

        when(notification1.getUserId()).thenReturn(userId);
        when(notification2.getUserId()).thenReturn(userId + 1);

        repository.addNotification(notification1);
        repository.addNotification(notification2);

        List<Notification> receivedNotifications =
                repository.getUserNotifications(notification1.getUserId());

        assertNotNull(receivedNotifications);
        assertEquals(1, receivedNotifications.size());
    }

    @Test
    void getUserNotifications_ReturnsListOfUserNotifications_WhenEmpty() {

        when(notification1.getUserId()).thenReturn(userId);
        when(notification2.getUserId()).thenReturn(userId + 1);
        repository.addNotification(notification1);
        repository.addNotification(notification2);

        List<Notification> receivedNotifications =
                repository.getUserNotifications(userId + 2);

        assertNotNull(receivedNotifications);
        assertEquals(0, receivedNotifications.size());
    }

    @Test
    void getTaskNotifications_ReturnsListOfTaskNotifications_WhenNotEmpty() {

        when(notification1.getTaskId()).thenReturn(taskId);
        when(notification2.getTaskId()).thenReturn(taskId + 1);

        repository.addNotification(notification1);
        repository.addNotification(notification2);

        List<Notification> receivedNotifications =
                repository.getTaskNotifications(taskId);

        assertNotNull(receivedNotifications);
        assertEquals(1, receivedNotifications.size());
    }

    @Test
    void getTaskNotifications_ReturnsListOfTaskNotifications_WhenEmpty() {

        when(notification1.getTaskId()).thenReturn(taskId);
        when(notification2.getTaskId()).thenReturn(taskId + 1);

        repository.addNotification(notification1);
        repository.addNotification(notification2);

        List<Notification> receivedNotifications =
                repository.getTaskNotifications(taskId + 3);

        assertNotNull(receivedNotifications);
        assertEquals(0, receivedNotifications.size());
    }

    @Test
    void addNotification_ReturnsAddedNotification() {

        Notification addedNotification = repository.addNotification(notification1);

        assertNotNull(addedNotification);
        assertEquals(notification1, addedNotification);
    }
}
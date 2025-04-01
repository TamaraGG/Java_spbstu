package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InMemoryNotificationDAOTest {

    private static final Notification notification1 = mock(Notification.class);
    private static final Notification notification2 = mock(Notification.class);

    private static final long USER_ID = 4L;
    private static final long TASK_ID = 2L;

    private InMemoryNotificationDAO repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNotificationDAO();
    }

    @Test
    void InMemoryNotificationDAO_GetAllNotifications_ReturnListOfNotifications() {
        repository.addNotification(notification1);
        repository.addNotification(notification2);

        List<Notification> receivedNotifications = repository.getAllNotifications();

        assertNotNull(receivedNotifications);
        assertEquals(2, receivedNotifications.size());
    }

    @Test
    void InMemoryNotificationDAO_GetUserNotifications_ReturnListOfNotifications() {
        when(notification1.getUserId()).thenReturn(USER_ID);
        when(notification2.getUserId()).thenReturn(USER_ID);
        repository.addNotification(notification1);
        repository.addNotification(notification2);

        List<Notification> receivedNotifications = repository.getUserNotifications(USER_ID);

        assertNotNull(receivedNotifications);
        assertEquals(2, receivedNotifications.size());
    }

    @Test
    void InMemoryNotificationDAO_GetTaskNotifications_ReturnListOfNotifications() {
        when(notification1.getTaskId()).thenReturn(TASK_ID);
        when(notification2.getTaskId()).thenReturn(TASK_ID);
        repository.addNotification(notification1);
        repository.addNotification(notification2);

        List<Notification> receivedNotifications = repository.getTaskNotifications(TASK_ID);

        assertNotNull(receivedNotifications);
        assertEquals(2, receivedNotifications.size());
    }

    @Test
    void InMemoryNotificationDAO_AddNotification_ReturnAddedTask() {

        Notification addedNotification = repository.addNotification(notification1);

        assertNotNull(addedNotification);
        assertEquals(notification1, addedNotification);

    }
}
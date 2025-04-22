package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InMemoryNotificationDAOTest {

    private InMemoryNotificationDAO repository;

    private static final long TASK_ID_1 = 100L;
    private static final long TASK_ID_2 = 200L;
    private static final long NOTIFICATION_ID_1 = 1L;
    private static final long NOTIFICATION_ID_2 = 2L;
    private static final long NOTIFICATION_ID_3 = 3L;
    private static final long NON_EXISTENT_ID = 999L;

    private static final Notification NOTIFICATION_1 = Notification.builder()
            .notificationId(NOTIFICATION_ID_1)
            .taskId(TASK_ID_1)
            .text("Notification 1 for Task 1")
            .date(LocalDateTime.now().minusHours(2))
            .build();

    private static final Notification NOTIFICATION_2 = Notification.builder()
            .notificationId(NOTIFICATION_ID_2)
            .taskId(TASK_ID_1)
            .text("Notification 2 for Task 1")
            .date(LocalDateTime.now().minusHours(1))
            .build();

    private static final Notification NOTIFICATION_3_OTHER_TASK = Notification.builder()
            .notificationId(NOTIFICATION_ID_3)
            .taskId(TASK_ID_2)
            .text("Notification 3 for Task 2")
            .date(LocalDateTime.now())
            .build();

    private static final Notification NOTIFICATION_1_DUPLICATE_ID = Notification.builder()
            .notificationId(NOTIFICATION_ID_1)
            .taskId(TASK_ID_2)
            .text("Duplicate ID Notification")
            .date(LocalDateTime.now())
            .build();


    @BeforeEach
    void setUp() {
        repository = new InMemoryNotificationDAO();
    }

    // getAllNotifications

    @Test
    void getAllNotifications_ReturnsListOfNotifications_WhenNotEmpty() {
        repository.addNotification(NOTIFICATION_1);
        repository.addNotification(NOTIFICATION_3_OTHER_TASK);

        List<Notification> receivedNotifications = repository.findAllNotifications();

        assertNotNull(receivedNotifications);
        assertEquals(2, receivedNotifications.size());
        assertTrue(receivedNotifications.contains(NOTIFICATION_1));
        assertTrue(receivedNotifications.contains(NOTIFICATION_3_OTHER_TASK));
    }

    @Test
    void getAllNotifications_ReturnsEmptyList_WhenEmpty() {
        List<Notification> receivedNotifications = repository.findAllNotifications();

        assertNotNull(receivedNotifications);
        assertTrue(receivedNotifications.isEmpty());
    }

    // findNotificationsByTaskId

    @Test
    void findNotificationsByTaskId_ReturnsListOfTaskNotifications_WhenTaskHasNotifications() {
        repository.addNotification(NOTIFICATION_1);
        repository.addNotification(NOTIFICATION_2);
        repository.addNotification(NOTIFICATION_3_OTHER_TASK);

        List<Notification> task1Notifications = repository.findNotificationsByTaskId(TASK_ID_1);

        assertNotNull(task1Notifications);
        assertEquals(2, task1Notifications.size());
        assertTrue(task1Notifications.contains(NOTIFICATION_1));
        assertTrue(task1Notifications.contains(NOTIFICATION_2));
        assertFalse(task1Notifications.contains(NOTIFICATION_3_OTHER_TASK));
    }

    @Test
    void findNotificationsByTaskId_ReturnsEmptyList_WhenTaskHasNoNotifications() {
        repository.addNotification(NOTIFICATION_1);

        List<Notification> task2Notifications = repository.findNotificationsByTaskId(TASK_ID_2);

        assertNotNull(task2Notifications);
        assertTrue(task2Notifications.isEmpty());
    }

    @Test
    void findNotificationsByTaskId_ReturnsEmptyList_WhenNoNotificationsExist() {
        List<Notification> task1Notifications = repository.findNotificationsByTaskId(TASK_ID_1);

        assertNotNull(task1Notifications);
        assertTrue(task1Notifications.isEmpty());
    }

    // addNotification

    @Test
    void addNotification_ReturnsAddedNotification_WhenValidAndUnique() {
        Notification addedNotification = repository.addNotification(NOTIFICATION_1);

        assertNotNull(addedNotification);
        assertEquals(NOTIFICATION_1, addedNotification);
        assertEquals(1, repository.findAllNotifications().size());
        assertTrue(repository.existsById(NOTIFICATION_ID_1));
        assertEquals(Optional.of(NOTIFICATION_1), repository.findNotificationById(NOTIFICATION_ID_1));
    }

    @Test
    void addNotification_ThrowsDuplicateResourceException_WhenIdExists() {
        repository.addNotification(NOTIFICATION_1);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> repository.addNotification(NOTIFICATION_1_DUPLICATE_ID)
        );

        assertTrue(exception.getMessage().contains("notification id " + NOTIFICATION_ID_1 + " already exists"));
        assertEquals(1, repository.findAllNotifications().size());
    }

    @Test
    void addNotification_ThrowsIllegalArgumentException_WhenNotificationIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.addNotification(null)
        );
        assertEquals("notification cannot be null", exception.getMessage());
        assertTrue(repository.findAllNotifications().isEmpty());
    }

    // findNotificationById

    @Test
    void findNotificationById_ReturnsOptionalWithNotification_WhenExists() {
        repository.addNotification(NOTIFICATION_1);

        Optional<Notification> foundOpt = repository.findNotificationById(NOTIFICATION_ID_1);

        assertTrue(foundOpt.isPresent());
        assertEquals(NOTIFICATION_1, foundOpt.get());
    }

    @Test
    void findNotificationById_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.addNotification(NOTIFICATION_1);

        Optional<Notification> foundOpt = repository.findNotificationById(NON_EXISTENT_ID);

        assertTrue(foundOpt.isEmpty());
    }

    @Test
    void findNotificationById_ReturnsEmptyOptional_WhenListIsEmpty() {
        Optional<Notification> foundOpt = repository.findNotificationById(NOTIFICATION_ID_1);
        assertTrue(foundOpt.isEmpty());
    }


    // deleteNotification

    @Test
    void deleteNotification_ReturnsTrueAndRemovesNotification_WhenExists() {
        repository.addNotification(NOTIFICATION_1);
        repository.addNotification(NOTIFICATION_2);
        assertEquals(2, repository.findAllNotifications().size());

        boolean result = repository.deleteNotification(NOTIFICATION_ID_1);

        assertTrue(result);
        assertEquals(1, repository.findAllNotifications().size());
        assertFalse(repository.existsById(NOTIFICATION_ID_1));
        assertTrue(repository.existsById(NOTIFICATION_ID_2));
    }

    @Test
    void deleteNotification_ReturnsFalse_WhenDoesNotExist() {
        repository.addNotification(NOTIFICATION_1);
        assertEquals(1, repository.findAllNotifications().size());

        boolean result = repository.deleteNotification(NON_EXISTENT_ID);

        assertFalse(result);
        assertEquals(1, repository.findAllNotifications().size());
        assertTrue(repository.existsById(NOTIFICATION_ID_1));
    }

    // existsById

    @Test
    void existsById_ReturnsTrue_WhenExists() {
        repository.addNotification(NOTIFICATION_1);
        assertTrue(repository.existsById(NOTIFICATION_ID_1));
    }

    @Test
    void existsById_ReturnsFalse_WhenDoesNotExist() {
        repository.addNotification(NOTIFICATION_1);
        assertFalse(repository.existsById(NON_EXISTENT_ID));
    }

    @Test
    void existsById_ReturnsFalse_WhenEmpty() {
        assertFalse(repository.existsById(NOTIFICATION_ID_1));
    }


}
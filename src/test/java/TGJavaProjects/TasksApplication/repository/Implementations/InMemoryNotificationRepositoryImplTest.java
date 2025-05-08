package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryNotificationRepositoryImpl;
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
class InMemoryNotificationRepositoryImplTest {

    private InMemoryNotificationRepositoryImpl repository;

    private Notification notificationToSave1User1Task1;
    private Notification notificationToSave2User1Task1;
    private Notification notificationToSave1User2Task2;

    private static final long USER_ID_1 = 1L;
    private static final long USER_ID_2 = 2L;
    private static final long TASK_ID_1 = 10L;
    private static final long TASK_ID_2 = 20L;

    private static final long NON_EXISTENT_NOTIFICATION_ID = 999L;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNotificationRepositoryImpl();

        notificationToSave1User1Task1 = Notification.builder()
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text("Notification 1 for User 1, Task 1")
                .date(LocalDateTime.now().minusHours(2))
                .isRead(false)
                .build();

        notificationToSave2User1Task1 = Notification.builder()
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text("Notification 2 for User 1, Task 1")
                .date(LocalDateTime.now().minusHours(1))
                .isRead(true)
                .build();

        notificationToSave1User2Task2 = Notification.builder()
                .userId(USER_ID_2)
                .taskId(TASK_ID_2)
                .text("Notification 1 for User 2, Task 2")
                .date(LocalDateTime.now())
                .isRead(false)
                .build();
    }

    // findAllNotifications

    @Test
    void findAllNotifications_ReturnsListOfNotifications_WhenNotEmpty() {
        repository.saveNotification(notificationToSave1User1Task1);
        repository.saveNotification(notificationToSave1User2Task2);

        List<Notification> receivedNotifications = repository.findAllNotifications();

        assertNotNull(receivedNotifications);
        assertEquals(2, receivedNotifications.size());
        assertTrue(receivedNotifications.stream()
                .anyMatch(n -> n.getText().equals(notificationToSave1User1Task1.getText())));
        assertTrue(receivedNotifications.stream()
                .anyMatch(n -> n.getText().equals(notificationToSave1User2Task2.getText())));
    }

    @Test
    void findAllNotifications_ReturnsEmptyList_WhenEmpty() {
        List<Notification> receivedNotifications = repository.findAllNotifications();
        assertNotNull(receivedNotifications);
        assertTrue(receivedNotifications.isEmpty());
    }

    // findNotificationById

    @Test
    void findNotificationById_ReturnsOptionalWithNotification_WhenExists() {
        Notification savedNotification = repository.saveNotification(notificationToSave1User1Task1);
        Optional<Notification> foundOpt = repository.findNotificationById(savedNotification.getNotificationId());
        assertTrue(foundOpt.isPresent());
        assertEquals(savedNotification.getText(), foundOpt.get().getText());
    }

    @Test
    void findNotificationById_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.saveNotification(notificationToSave1User1Task1);
        Optional<Notification> foundOpt = repository.findNotificationById(NON_EXISTENT_NOTIFICATION_ID);
        assertTrue(foundOpt.isEmpty());
    }

    // findNotificationsByUserId

    @Test
    void findNotificationsByUserId_ReturnsListOfUserNotifications() {
        Notification savedN1U1 = repository.saveNotification(notificationToSave1User1Task1);
        Notification savedN2U1 = repository.saveNotification(notificationToSave2User1Task1);
        repository.saveNotification(notificationToSave1User2Task2);

        List<Notification> user1Notifications = repository.findNotificationsByUserId(USER_ID_1);

        assertNotNull(user1Notifications);
        assertEquals(2, user1Notifications.size());
        assertTrue(user1Notifications.stream()
                .anyMatch(n -> n.getNotificationId().equals(savedN1U1.getNotificationId())));
        assertTrue(user1Notifications.stream()
                .anyMatch(n -> n.getNotificationId().equals(savedN2U1.getNotificationId())));
    }

    @Test
    void findNotificationsByUserId_ReturnsEmptyList_WhenUserHasNoNotifications() {
        repository.saveNotification(notificationToSave1User2Task2);
        List<Notification> user1Notifications = repository.findNotificationsByUserId(USER_ID_1);
        assertNotNull(user1Notifications);
        assertTrue(user1Notifications.isEmpty());
    }

    // saveNotification (add)

    @Test
    void saveNotification_NewNotification_AssignsIdAndReturnsSavedNotification() {
        Notification savedNotification = repository.saveNotification(notificationToSave1User1Task1);

        assertNotNull(savedNotification);
        assertNotNull(savedNotification.getNotificationId());
        assertEquals(notificationToSave1User1Task1.getText(), savedNotification.getText());
        assertEquals(USER_ID_1, savedNotification.getUserId());
        assertEquals(TASK_ID_1, savedNotification.getTaskId());

        assertTrue(repository.existsById(savedNotification.getNotificationId()));
        assertEquals(Optional.of(savedNotification),
                repository.findNotificationById(savedNotification.getNotificationId()));
    }

    // saveNotification (update)

    @Test
    void saveNotification_ExistingNotification_UpdatesAndReturnsNotification() {
        Notification savedNotification = repository.saveNotification(notificationToSave1User1Task1);
        Long originalId = savedNotification.getNotificationId();

        Notification notificationToUpdate = Notification.builder()
                .notificationId(originalId)
                .userId(savedNotification.getUserId())
                .taskId(savedNotification.getTaskId())
                .text("Updated Notification Text")
                .date(savedNotification.getDate())
                .isRead(true)
                .build();

        Notification updatedNotification = repository.saveNotification(notificationToUpdate);

        assertNotNull(updatedNotification);
        assertEquals(originalId, updatedNotification.getNotificationId());
        assertEquals("Updated Notification Text", updatedNotification.getText());
        assertTrue(updatedNotification.getIsRead());

        Optional<Notification> foundOpt = repository.findNotificationById(originalId);
        assertTrue(foundOpt.isPresent());
        assertEquals("Updated Notification Text", foundOpt.get().getText());
        assertTrue(foundOpt.get().getIsRead());
    }

    @Test
    void saveNotification_ExistingNotification_ThrowsIllegalArgumentException_WhenUpdatingNonExisting() {
        Notification nonExistentNotificationToUpdate = Notification.builder()
                .notificationId(NON_EXISTENT_NOTIFICATION_ID)
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text("Ghost Notification")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.saveNotification(nonExistentNotificationToUpdate)
        );
        assertTrue(exception.getMessage()
                .contains("cannot update non-existing notification with id " + NON_EXISTENT_NOTIFICATION_ID));
    }

    // updateNotification

    @Test
    void updateNotification_UpdatesExistingNotification() {
        Notification savedNotification = repository.saveNotification(notificationToSave1User1Task1);
        Long notificationId = savedNotification.getNotificationId();

        savedNotification.setIsRead(true);
        savedNotification.setText("Marked as Read Notification");
        Notification updatedNotification = repository.updateNotification(savedNotification);

        assertNotNull(updatedNotification);
        assertEquals(notificationId, updatedNotification.getNotificationId());
        assertTrue(updatedNotification.getIsRead());
        assertEquals("Marked as Read Notification", updatedNotification.getText());

        Optional<Notification> foundOpt = repository.findNotificationById(notificationId);
        assertTrue(foundOpt.isPresent());
        assertTrue(foundOpt.get().getIsRead());
    }

    @Test
    void updateNotification_ThrowsIllegalArgumentException_WhenNotificationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> repository.updateNotification(null));
    }

    @Test
    void updateNotification_ThrowsIllegalArgumentException_WhenNotificationIdIsNull() {
        Notification notificationWithNullId = Notification.builder()
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text("text").build();
        assertThrows(IllegalArgumentException.class, () -> repository.updateNotification(notificationWithNullId));
    }

    @Test
    void updateNotification_ThrowsIllegalArgumentException_WhenNotificationNotFoundForUpdate() {
        Notification nonExistentNotification = Notification.builder()
                .notificationId(NON_EXISTENT_NOTIFICATION_ID)
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text("text").build();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.updateNotification(nonExistentNotification)
        );
        assertTrue(exception.getMessage()
                .contains("notification with id " + NON_EXISTENT_NOTIFICATION_ID + " not found for update."));
    }

    // existsById

    @Test
    void existsById_ReturnsTrue_WhenExists() {
        Notification savedNotification = repository.saveNotification(notificationToSave1User1Task1);
        assertTrue(repository.existsById(savedNotification.getNotificationId()));
    }

    @Test
    void existsById_ReturnsFalse_WhenDoesNotExist() {
        assertFalse(repository.existsById(NON_EXISTENT_NOTIFICATION_ID));
    }
}
package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Profile("!inmemory")
class NotificationServiceH2ImplTest {

    @InjectMocks
    private JpaNotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    private Notification notification1;
    private Notification notification2;
    private Notification notificationToCreate;
    private Task task1;
    private User user1;


    private static final long USER_ID_1 = 1L;
    private static final long TASK_ID_1 = 10L;
    private static final long NOTIFICATION_ID_1 = 100L;
    private static final long NOTIFICATION_ID_2 = 200L;
    private static final long NON_EXISTENT_TASK_ID = 4L;
    private static final long NON_EXISTENT_USER_ID = 3L;
    private static final long NON_EXISTENT_NOTIFICATION_ID = 9L;


    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userId(USER_ID_1)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .build();

        task1 = Task.builder()
                .taskId(TASK_ID_1)
                .user(user1)
                .taskText("Test Task 1")
                .creationDate(LocalDateTime.now().minusDays(1))
                .isComplete(false)
                .build();

        notificationToCreate = Notification.builder()
                .task(task1)
                .text("New Notification Text")
                .build();

        notification1 = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .task(task1)
                .text("Notification 1 Text")
                .date(LocalDateTime.now().minusHours(1))
                .build();

        notification2 = Notification.builder()
                .notificationId(NOTIFICATION_ID_2)
                .task(task1)
                .text("Notification 2 Text")
                .date(LocalDateTime.now())
                .build();
    }

    // findAllNotifications
    @Test
    void findAllNotifications_ShouldReturnListOfNotifications() {
        List<Notification> expected = List.of(notification1, notification2);
        when(notificationRepository.findAll()).thenReturn(expected);

        List<Notification> actual = notificationService.findAllNotifications();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void findAllNotifications_ShouldReturnEmptyList_WhenNoneExist() {
        when(notificationRepository.findAll()).thenReturn(Collections.emptyList());

        List<Notification> actual = notificationService.findAllNotifications();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(notificationRepository, times(1)).findAll();
    }

    // findNotificationsByUserId
    @Test
    void findNotificationsByUserId_ShouldReturnNotifications_WhenUserExists() {
        List<Notification> expected = List.of(notification1, notification2);
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(notificationRepository.findByTaskUserUserId(USER_ID_1)).thenReturn(expected);

        List<Notification> actual = notificationService.findNotificationsByUserId(USER_ID_1);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(notificationRepository, times(1)).findByTaskUserUserId(USER_ID_1);
    }

    @Test
    void findNotificationsByUserId_ShouldReturnEmptyList_WhenUserExistsButHasNoNotifications() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(notificationRepository.findByTaskUserUserId(USER_ID_1)).thenReturn(Collections.emptyList());

        List<Notification> actual = notificationService.findNotificationsByUserId(USER_ID_1);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(notificationRepository, times(1)).findByTaskUserUserId(USER_ID_1);
    }

    @Test
    void findNotificationsByUserId_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.findNotificationsByUserId(NON_EXISTENT_USER_ID)
        );
        assertTrue(exception.getMessage().contains("User with id " + NON_EXISTENT_USER_ID + " not found"));
        verify(userRepository, times(1)).existsById(NON_EXISTENT_USER_ID);
        verify(notificationRepository, never()).findByTaskUserUserId(anyLong());
    }


    // findNotificationsByTaskId
    @Test
    void findNotificationsByTaskId_ShouldReturnNotifications_WhenTaskExists() {
        List<Notification> expected = List.of(notification1, notification2);
        when(taskRepository.existsById(TASK_ID_1)).thenReturn(true);
        when(notificationRepository.findByTaskTaskId(TASK_ID_1)).thenReturn(expected);

        List<Notification> actual = notificationService.findNotificationsByTaskId(TASK_ID_1);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(taskRepository, times(1)).existsById(TASK_ID_1);
        verify(notificationRepository, times(1)).findByTaskTaskId(TASK_ID_1);
    }

    @Test
    void findNotificationsByTaskId_ShouldReturnEmptyList_WhenTaskExistsButHasNoNotifications() {
        when(taskRepository.existsById(TASK_ID_1)).thenReturn(true);
        when(notificationRepository.findByTaskTaskId(TASK_ID_1)).thenReturn(Collections.emptyList());

        List<Notification> actual = notificationService.findNotificationsByTaskId(TASK_ID_1);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(taskRepository, times(1)).existsById(TASK_ID_1);
        verify(notificationRepository, times(1)).findByTaskTaskId(TASK_ID_1);
    }

    @Test
    void findNotificationsByTaskId_ShouldThrowResourceNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(NON_EXISTENT_TASK_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.findNotificationsByTaskId(NON_EXISTENT_TASK_ID)
        );
        assertTrue(exception.getMessage().contains("task with id " + NON_EXISTENT_TASK_ID + " not found"));
        verify(taskRepository, times(1)).existsById(NON_EXISTENT_TASK_ID);
        verify(notificationRepository, never()).findByTaskTaskId(anyLong());
    }

    // findNotificationById
    @Test
    void findNotificationById_ShouldReturnNotification_WhenExists() {
        when(notificationRepository.findById(NOTIFICATION_ID_1)).thenReturn(Optional.of(notification1));

        Notification actual = notificationService.findNotificationById(NOTIFICATION_ID_1);

        assertNotNull(actual);
        assertEquals(notification1, actual);
        verify(notificationRepository, times(1)).findById(NOTIFICATION_ID_1);
    }

    @Test
    void findNotificationById_ShouldThrowResourceNotFoundException_WhenDoesNotExist() {
        when(notificationRepository.findById(NON_EXISTENT_NOTIFICATION_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.findNotificationById(NON_EXISTENT_NOTIFICATION_ID)
        );
        assertTrue(exception.getMessage().contains("notification with id " + NON_EXISTENT_NOTIFICATION_ID + " not found"));
        verify(notificationRepository, times(1)).findById(NON_EXISTENT_NOTIFICATION_ID);
    }

    // addNotification
    @Test
    void addNotification_ShouldReturnSavedNotification_WhenValidAndTaskExists() {
        when(taskRepository.findById(TASK_ID_1)).thenReturn(Optional.of(task1));
        Notification savedNotification = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .task(task1)
                .text(notificationToCreate.getText())
                .date(LocalDateTime.now())
                .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notificationService.addNotification(notificationToCreate);

        assertNotNull(result);
        assertEquals(NOTIFICATION_ID_1, result.getNotificationId());
        assertEquals(task1, result.getTask());
        assertNotNull(result.getDate());
        verify(taskRepository, times(1)).findById(TASK_ID_1);
        verify(notificationRepository, times(1)).save(notificationToCreate);
    }

    @Test
    void addNotification_ShouldThrowIllegalArgumentException_WhenNotificationIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificationService.addNotification(null)
        );
        assertEquals("notification cannot be null", exception.getMessage());
        verifyNoInteractions(taskRepository, notificationRepository);
    }

    @Test
    void addNotification_ShouldThrowIllegalArgumentException_WhenNotificationIdIsNotNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificationService.addNotification(notification1)
        );
        assertEquals("notification id must be null to create notification", exception.getMessage());
        verifyNoInteractions(taskRepository, notificationRepository);
    }

    @Test
    void addNotification_ShouldThrowIllegalArgumentException_WhenTextIsBlank() {
        notificationToCreate.setText(" ");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificationService.addNotification(notificationToCreate)
        );
        assertEquals("notification text cannot be blank", exception.getMessage());
        verifyNoInteractions(taskRepository, notificationRepository);
    }

    @Test
    void addNotification_ShouldThrowIllegalArgumentException_WhenTaskIsNull() {
        notificationToCreate.setTask(null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificationService.addNotification(notificationToCreate)
        );
        assertEquals("task association (with ID) is required for notification", exception.getMessage());
        verifyNoInteractions(taskRepository, notificationRepository);
    }

    @Test
    void addNotification_ShouldThrowIllegalArgumentException_WhenTaskIdIsNull() {
        notificationToCreate.getTask().setTaskId(null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificationService.addNotification(notificationToCreate)
        );
        assertEquals("task association (with ID) is required for notification", exception.getMessage());
        verifyNoInteractions(taskRepository, notificationRepository);
    }

    @Test
    void addNotification_ShouldThrowResourceNotFoundException_WhenTaskNotFound() {
        when(taskRepository.findById(TASK_ID_1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.addNotification(notificationToCreate)
        );
        assertTrue(exception.getMessage().contains("cannot add notification. associated task with id " + TASK_ID_1 + " not found"));
        verify(taskRepository, times(1)).findById(TASK_ID_1);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void addNotification_ShouldRethrowException_WhenRepositorySaveFails() {
        when(taskRepository.findById(TASK_ID_1)).thenReturn(Optional.of(task1));
        when(notificationRepository.save(any(Notification.class))).thenThrow(new RuntimeException("Simulated save error"));

        assertThrows(RuntimeException.class, () -> notificationService.addNotification(notificationToCreate));
        verify(taskRepository, times(1)).findById(TASK_ID_1);
        verify(notificationRepository, times(1)).save(notificationToCreate);
    }

    // deleteNotification
    @Test
    void deleteNotification_ShouldCompleteSuccessfully_WhenNotificationExists() {
        when(notificationRepository.existsById(NOTIFICATION_ID_1)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(NOTIFICATION_ID_1);

        assertDoesNotThrow(() -> notificationService.deleteNotification(NOTIFICATION_ID_1));

        verify(notificationRepository, times(1)).existsById(NOTIFICATION_ID_1);
        verify(notificationRepository, times(1)).deleteById(NOTIFICATION_ID_1);
    }

    @Test
    void deleteNotification_ShouldThrowResourceNotFoundException_WhenNotificationDoesNotExist() {
        when(notificationRepository.existsById(NON_EXISTENT_NOTIFICATION_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.deleteNotification(NON_EXISTENT_NOTIFICATION_ID)
        );
        assertTrue(exception.getMessage().contains("delete error. notification with id " + NON_EXISTENT_NOTIFICATION_ID + " not found"));
        verify(notificationRepository, times(1)).existsById(NON_EXISTENT_NOTIFICATION_ID);
        verify(notificationRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteNotification_ShouldHandleException_WhenRepositoryDeleteThrows() {
        when(notificationRepository.existsById(NOTIFICATION_ID_1)).thenReturn(true);
        doThrow(new RuntimeException("Simulated delete error")).when(notificationRepository).deleteById(NOTIFICATION_ID_1);

        assertThrows(RuntimeException.class, () -> notificationService.deleteNotification(NOTIFICATION_ID_1));

        verify(notificationRepository, times(1)).existsById(NOTIFICATION_ID_1);
        verify(notificationRepository, times(1)).deleteById(NOTIFICATION_ID_1);
    }
}
package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryNotificationImplTest {

    @InjectMocks
    private InMemoryNotificationImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;

    private Notification notification1, notification2, notification1Read;
    private Notification notificationToCreate;
    private static final long USER_ID_1 = 1L;
    private static final long TASK_ID_1 = 100L;
    private static final long NOTIFICATION_ID_1 = 10L;
    private static final long NOTIFICATION_ID_2 = 20L;
    private static final long NON_EXISTENT_USER_ID = 99L;
    private static final long NON_EXISTENT_NOTIFICATION_ID = 999L;
    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        notificationToCreate = Notification.builder()
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text("New Notification Text")
                .date(NOW)
                .isRead(false)
                .build();

        notification1 = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text("Notification 1 Text")
                .date(NOW.minusHours(1))
                .isRead(false)
                .build();

        notification2 = Notification.builder()
                .notificationId(NOTIFICATION_ID_2)
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text("Notification 2 Text")
                .date(NOW)
                .isRead(true)
                .build();

        notification1Read = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .userId(USER_ID_1)
                .taskId(TASK_ID_1)
                .text(notification1.getText())
                .date(notification1.getDate())
                .isRead(true)
                .build();
    }

    // findAllNotifications

    @Test
    void findAllNotifications_ReturnsListOfNotificationsFromRepository() {
        List<Notification> expected = List.of(notification1, notification2);
        when(notificationRepository.findAll()).thenReturn(expected);

        List<Notification> actual = notificationService.findAllNotifications();

        assertEquals(expected, actual);
        verify(notificationRepository, times(1)).findAll();
    }

    // getAllNotificationsByUserId

    @Test
    void getAllNotificationsByUserId_ReturnsUserNotifications_WhenUserExists() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        List<Notification> expectedNotifications = List.of(notification1, notification2);
        when(notificationRepository.findNotificationsByUserId(USER_ID_1))
                .thenReturn(expectedNotifications);

        List<Notification> actualNotifications = notificationService
                .getAllNotificationsByUserId(USER_ID_1);

        assertNotNull(actualNotifications);
        assertEquals(expectedNotifications, actualNotifications);
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(notificationRepository, times(1)).findNotificationsByUserId(USER_ID_1);
    }

    @Test
    void getAllNotificationsByUserId_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.getAllNotificationsByUserId(NON_EXISTENT_USER_ID));
        verify(notificationRepository, never()).findNotificationsByUserId(anyLong());
    }

    // getPendingNotificationsByUserId

    @Test
    void getPendingNotificationsByUserId_ReturnsOnlyUnreadNotifications() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        List<Notification> notificationsFromRepo = List.of(notification1, notification2);
        when(notificationRepository.findNotificationsByUserId(USER_ID_1))
                .thenReturn(notificationsFromRepo);

        List<Notification> result = notificationService.getPendingNotificationsByUserId(USER_ID_1);

        assertEquals(1, result.size());
        assertTrue(result.contains(notification1));
        assertFalse(result.contains(notification2));
        verify(notificationRepository, times(1))
                .findNotificationsByUserId(USER_ID_1);
    }

    @Test
    void getPendingNotificationsByUserId_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.getPendingNotificationsByUserId(NON_EXISTENT_USER_ID));
        verify(notificationRepository, never()).findNotificationsByUserId(anyLong());
    }

    // addNotification

    @Test
    void addNotification_ReturnsNotification_WhenUserExistsAndDataValid() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        Notification savedNotificationWithId = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .userId(notificationToCreate.getUserId())
                .taskId(notificationToCreate.getTaskId())
                .text(notificationToCreate.getText())
                .date(notificationToCreate.getDate())
                .isRead(notificationToCreate.getIsRead())
                .build();

        when(notificationRepository.save(notificationToCreate)).thenReturn(savedNotificationWithId);

        Notification result = notificationService.addNotification(notificationToCreate);

        assertNotNull(result);
        assertEquals(savedNotificationWithId, result);
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(notificationRepository, times(1)).save(notificationToCreate);
    }

    @Test
    void addNotification_ThrowsIllegalArgument_WhenNotificationIsNull() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.addNotification(null));
        assertEquals("notification cannot be null.", exception.getMessage());
        verifyNoInteractions(userRepository, notificationRepository);
    }

    @Test
    void addNotification_ThrowsResourceNotFound_WhenUserDoesNotExist() {

        Notification notificationForNonExistentUser = Notification.builder()
                .userId(NON_EXISTENT_USER_ID)
                .text("test")
                .taskId(TASK_ID_1).build();

        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.addNotification(notificationForNonExistentUser));

        verify(notificationRepository, never()).save(any());
    }

    // markNotificationAsRead

    @Test
    void markNotificationAsRead_MarksNotificationAsRead_WhenValid() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        when(notificationRepository.findById(NOTIFICATION_ID_1))
                .thenReturn(Optional.of(notification1));

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.markNotificationAsRead(USER_ID_1, NOTIFICATION_ID_1);

        verify(notificationRepository, times(1))
                .findById(NOTIFICATION_ID_1);
        verify(notificationRepository, times(1))
                .save(argThat(notification ->
                        notification.getNotificationId().equals(NOTIFICATION_ID_1) &&
                                notification.getIsRead()
                ));
    }

    @Test
    void markNotificationAsRead_DoesNothing_WhenAlreadyRead() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(notificationRepository.findById(NOTIFICATION_ID_2))
                .thenReturn(Optional.of(notification2));

        notificationService.markNotificationAsRead(USER_ID_1, NOTIFICATION_ID_2);

        verify(notificationRepository, times(1)).findById(NOTIFICATION_ID_2);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markNotificationAsRead_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markNotificationAsRead(NON_EXISTENT_USER_ID, NOTIFICATION_ID_1));
        verify(notificationRepository, never()).findById(anyLong());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markNotificationAsRead_ThrowsResourceNotFound_WhenNotificationDoesNotExistForUser() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(notificationRepository.findById(NON_EXISTENT_NOTIFICATION_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService
                        .markNotificationAsRead(USER_ID_1, NON_EXISTENT_NOTIFICATION_ID));
        verify(notificationRepository, times(1))
                .findById(NON_EXISTENT_NOTIFICATION_ID);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markNotificationAsRead_ThrowsResourceNotFound_WhenNotificationExistsButNotForThisUser() {
        Notification notificationOfOtherUser = Notification.builder()
                .notificationId(NOTIFICATION_ID_1)
                .userId(USER_ID_1 + 5)
                .taskId(TASK_ID_1)
                .text("Other user notification")
                .isRead(false)
                .build();

        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(notificationRepository.findById(NOTIFICATION_ID_1)).thenReturn(Optional.of(notificationOfOtherUser));

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markNotificationAsRead(USER_ID_1, NOTIFICATION_ID_1));
        verify(notificationRepository, times(1)).findById(NOTIFICATION_ID_1);
        verify(notificationRepository, never()).save(any());
    }

}
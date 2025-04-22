package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.controller.NotificationController;
import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.InMemoryNotificationDAO;
import TGJavaProjects.TasksApplication.repository.InMemoryTaskDAO;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryNotificationImplTest {

    @InjectMocks
    private InMemoryNotificationImpl notificationService;

    @Mock
    private InMemoryTaskDAO taskDAO;
    @Mock
    private InMemoryNotificationDAO notificationDAO;

    private Notification notification1;
    private Notification notification2;
    private static final long TASK_ID_1 = 1L;
    private static final long NOTIFICATION_ID_1 = 10L;
    private static final long NOTIFICATION_ID_2 = 33L;
    private static final long NON_EXISTENT_TASK_ID = 14L;
    private static final long NON_EXISTENT_NOTIFICATION_ID = 28L;

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


    // findAllNotifications

    @Test
    void findAllNotifications_ReturnsListOfNotifications() {
        List<Notification> expectedNotifications = List.of(notification1, notification2);
        when(notificationDAO.findAllNotifications()).thenReturn(expectedNotifications);

        List<Notification> actualNotifications = notificationService.findAllNotifications();

        assertNotNull(actualNotifications);
        assertEquals(expectedNotifications, actualNotifications);
        assertEquals(2, actualNotifications.size());
        verify(notificationDAO, times(1)).findAllNotifications();
    }

    @Test
    void findAllNotifications_ReturnsEmptyList() {
        when(notificationDAO.findAllNotifications()).thenReturn(Collections.emptyList());

        List<Notification> actualNotifications = notificationService.findAllNotifications();

        assertNotNull(actualNotifications);
        assertTrue(actualNotifications.isEmpty());
        verify(notificationDAO, times(1)).findAllNotifications();
    }

    // findNotificationsByUserId

    @Test
    void findNotificationsByUserId_ReturnsEmptyList() {

    }


    // findNotificationsByTaskId

    @Test
    void findNotificationsByTaskId_ReturnsNotifications_WhenTaskExists() {
        List<Notification> expectedNotifications = List.of(notification1, notification2);
        when(taskDAO.existsById(TASK_ID_1)).thenReturn(true);
        when(notificationDAO.findNotificationsByTaskId(TASK_ID_1)).thenReturn(expectedNotifications);

        List<Notification> actualNotifications = notificationService.findNotificationsByTaskId(TASK_ID_1);

        assertNotNull(actualNotifications);
        assertEquals(expectedNotifications, actualNotifications);
        verify(taskDAO, times(1)).existsById(TASK_ID_1);
        verify(notificationDAO, times(1)).findNotificationsByTaskId(TASK_ID_1);
    }

    @Test
    void findNotificationsByTaskId_ReturnsEmptyList_WhenTaskExistsButNoNotifications() {
        when(taskDAO.existsById(TASK_ID_1)).thenReturn(true);
        when(notificationDAO.findNotificationsByTaskId(TASK_ID_1)).thenReturn(Collections.emptyList());

        List<Notification> actualNotifications = notificationService.findNotificationsByTaskId(TASK_ID_1);

        assertNotNull(actualNotifications);
        assertTrue(actualNotifications.isEmpty());
        verify(taskDAO, times(1)).existsById(TASK_ID_1);
        verify(notificationDAO, times(1)).findNotificationsByTaskId(TASK_ID_1);
    }

    @Test
    void findNotificationsByTaskId_ThrowsNotFound_WhenTaskDoesNotExist() {
        when(taskDAO.existsById(NON_EXISTENT_TASK_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.findNotificationsByTaskId(NON_EXISTENT_TASK_ID)
        );

        assertTrue(exception.getMessage().contains("cannot find notifications. task " + NON_EXISTENT_TASK_ID + " not found"));
        verify(taskDAO, times(1)).existsById(NON_EXISTENT_TASK_ID);
        verify(notificationDAO, never()).findNotificationsByTaskId(anyLong());
    }

    // findNotificationById

    @Test
    void findNotificationById_ReturnsNotification_WhenFound() {
        when(notificationDAO.findNotificationById(NOTIFICATION_ID_1)).thenReturn(Optional.of(notification1));

        Notification foundNotification = notificationService.findNotificationById(NOTIFICATION_ID_1);

        assertNotNull(foundNotification);
        assertEquals(notification1, foundNotification);
        verify(notificationDAO, times(1)).findNotificationById(NOTIFICATION_ID_1);
    }

    @Test
    void findNotificationById_ThrowsNotFound_WhenNotFound() {
        when(notificationDAO.findNotificationById(NON_EXISTENT_NOTIFICATION_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.findNotificationById(NON_EXISTENT_NOTIFICATION_ID)
        );

        assertTrue(exception.getMessage().contains("notification " + NON_EXISTENT_NOTIFICATION_ID + " not found"));
        verify(notificationDAO, times(1)).findNotificationById(NON_EXISTENT_NOTIFICATION_ID);
    }

    // addNotification

    @Test
    void addNotification_ReturnsNotification_WhenValidAndTaskExists() {
        when(taskDAO.existsById(TASK_ID_1)).thenReturn(true);
        when(notificationDAO.addNotification(any(Notification.class))).thenReturn(notification1);

        Notification addedNotification = notificationService.addNotification(notification1);

        assertNotNull(addedNotification);
        assertEquals(notification1, addedNotification);
        verify(taskDAO, times(1)).existsById(TASK_ID_1);
        verify(notificationDAO, times(1)).addNotification(notification1);
    }

    @Test
    void addNotification_ThrowsNotFound_WhenTaskDoesNotExist() {
        when(taskDAO.existsById(TASK_ID_1)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.addNotification(notification1)
        );

        assertTrue(exception.getMessage().contains("cannot find notification. task " + TASK_ID_1 + "not found"));
        verify(taskDAO, times(1)).existsById(TASK_ID_1);
        verify(notificationDAO, never()).addNotification(any(Notification.class));
    }

    @Test
    void addNotification_ThrowsDuplicateException_WhenDaoThrows() {
        when(taskDAO.existsById(TASK_ID_1)).thenReturn(true);
        when(notificationDAO.addNotification(notification1)).thenThrow(new DuplicateResourceException("ID exists"));

        assertThrows(DuplicateResourceException.class, () -> notificationService.addNotification(notification1));
        verify(taskDAO, times(1)).existsById(TASK_ID_1);
        verify(notificationDAO, times(1)).addNotification(notification1);
    }

    @Test
    void addNotification_ThrowsIllegalArgument_WhenNotificationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> notificationService.addNotification(null));
        verifyNoInteractions(taskDAO, notificationDAO);
    }

    // deleteNotification

    @Test
    void deleteNotification_CompletesNormally_WhenSuccessful() {
        when(notificationDAO.existsById(NOTIFICATION_ID_1)).thenReturn(true);
        when(notificationDAO.deleteNotification(NOTIFICATION_ID_1)).thenReturn(true);

        assertDoesNotThrow(() -> notificationService.deleteNotification(NOTIFICATION_ID_1));

        verify(notificationDAO, times(1)).existsById(NOTIFICATION_ID_1);
        verify(notificationDAO, times(1)).deleteNotification(NOTIFICATION_ID_1);
    }

    @Test
    void deleteNotification_ThrowsNotFound_WhenNotificationDoesNotExist() {
        when(notificationDAO.existsById(NON_EXISTENT_NOTIFICATION_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.deleteNotification(NON_EXISTENT_NOTIFICATION_ID)
        );
        assertTrue(exception.getMessage().contains("delete error. notification with id " + NON_EXISTENT_NOTIFICATION_ID + " not found"));
        verify(notificationDAO, times(1)).existsById(NON_EXISTENT_NOTIFICATION_ID);
        verify(notificationDAO, never()).deleteNotification(anyLong());
    }

    @Test
    void deleteNotification_ThrowsRuntimeException_WhenDaoDeleteFails() {
        when(notificationDAO.existsById(NOTIFICATION_ID_1)).thenReturn(true);
        when(notificationDAO.deleteNotification(NOTIFICATION_ID_1)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> notificationService.deleteNotification(NOTIFICATION_ID_1)
        );
        assertTrue(exception.getMessage().contains("delete failed for notification " + NOTIFICATION_ID_1));
        verify(notificationDAO, times(1)).existsById(NOTIFICATION_ID_1);
        verify(notificationDAO, times(1)).deleteNotification(NOTIFICATION_ID_1);
    }


}
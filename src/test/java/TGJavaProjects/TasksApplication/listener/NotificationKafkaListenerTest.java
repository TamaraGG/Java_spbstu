package TGJavaProjects.TasksApplication.listener;

import TGJavaProjects.TasksApplication.event.TaskCreatedEvent;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationKafkaListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationKafkaListener notificationKafkaListener;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Test
    void handleTaskCreatedEvent_shouldCallNotificationServiceAddNotification() {
        TaskCreatedEvent event = new TaskCreatedEvent(1L, 2L, "Test Task from Kafka");

        Notification mockSavedNotification = Notification.builder()
                .notificationId(100L)
                .userId(event.getUserId())
                .taskId(event.getTaskId())
                .text("New task created: " + event.getTaskText())
                .date(LocalDateTime.now())
                .isRead(false)
                .build();

        when(notificationService.addNotification(any(Notification.class)))
                .thenReturn(mockSavedNotification);

        notificationKafkaListener.handleTaskCreatedEvent(event);

        verify(notificationService, times(1)).addNotification(notificationCaptor.capture());
        Notification capturedNotificationArgument = notificationCaptor.getValue();

        assertNotNull(capturedNotificationArgument);
        assertEquals(event.getTaskId(), capturedNotificationArgument.getTaskId());
        assertEquals(event.getUserId(), capturedNotificationArgument.getUserId());
        assertEquals("New task created: " + event.getTaskText(), capturedNotificationArgument.getText());
        assertFalse(capturedNotificationArgument.getIsRead());
    }

    @Test
    void handleTaskCreatedEvent_whenNotificationServiceThrowsException_shouldCatchAndLog() {
        TaskCreatedEvent event = new TaskCreatedEvent(3L, 4L, "Task causing error");
        RuntimeException simulatedException = new RuntimeException("Database connection failed");
        when(notificationService.addNotification(any(Notification.class))).thenThrow(simulatedException);

        assertDoesNotThrow(() -> notificationKafkaListener.handleTaskCreatedEvent(event));

        verify(notificationService, times(1)).addNotification(any(Notification.class));
    }
}
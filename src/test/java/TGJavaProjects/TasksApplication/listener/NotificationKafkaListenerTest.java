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
// import org.slf4j.Logger; // Для мокирования логгера, если нужно

import java.time.LocalDateTime; // Добавим для создания мок-объекта Notification

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
        // Arrange
        TaskCreatedEvent event = new TaskCreatedEvent(1L, 2L, "Test Task from Kafka");

        // Создаем мок-объект Notification, который будет возвращен методом addNotification
        Notification mockSavedNotification = Notification.builder()
                .notificationId(100L) // Пример ID
                .userId(event.getUserId())
                .taskId(event.getTaskId())
                .text("New task created: " + event.getTaskText())
                .date(LocalDateTime.now()) // Пример даты
                .isRead(false)
                .build();

        // Используем when(...).thenReturn(...) так как addNotification возвращает Notification
        when(notificationService.addNotification(any(Notification.class)))
                .thenReturn(mockSavedNotification);

        // Act
        notificationKafkaListener.handleTaskCreatedEvent(event);

        // Assert
        verify(notificationService, times(1)).addNotification(notificationCaptor.capture());
        Notification capturedNotificationArgument = notificationCaptor.getValue(); // Это аргумент, переданный в addNotification

        assertNotNull(capturedNotificationArgument);
        assertEquals(event.getTaskId(), capturedNotificationArgument.getTaskId());
        assertEquals(event.getUserId(), capturedNotificationArgument.getUserId());
        assertEquals("New task created: " + event.getTaskText(), capturedNotificationArgument.getText());
        // Проверяем значения по умолчанию, которые должны быть установлены перед вызовом save
        assertFalse(capturedNotificationArgument.getIsRead());
        // Дата в capturedNotificationArgument будет той, что установлена в Notification.builder() внутри listener'а
        // Если важно проверить, что вернул сам метод addNotification (хотя в listener'е результат не используется),
        // то это был бы mockSavedNotification.
    }

    @Test
    void handleTaskCreatedEvent_whenNotificationServiceThrowsException_shouldCatchAndLog() {
        // Arrange
        TaskCreatedEvent event = new TaskCreatedEvent(3L, 4L, "Task causing error");
        RuntimeException simulatedException = new RuntimeException("Database connection failed");
        // addNotification возвращает значение, поэтому when(...).thenThrow(...)
        when(notificationService.addNotification(any(Notification.class))).thenThrow(simulatedException);

        // Act & Assert
        assertDoesNotThrow(() -> notificationKafkaListener.handleTaskCreatedEvent(event));

        verify(notificationService, times(1)).addNotification(any(Notification.class));
    }
}
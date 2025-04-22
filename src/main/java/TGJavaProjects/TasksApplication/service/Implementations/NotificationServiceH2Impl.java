package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("H2")
public class NotificationServiceH2Impl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final TaskRepository taskRepository;

    @Override
    public List<Notification> findAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> findNotificationsByUserId(long userId) {
        return List.of();
    }

    @Override
    public List<Notification> findNotificationsByTaskId(long taskId)
            throws ResourceNotFoundException {

        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException(
                    "cannot find notifications. task with id " + taskId + " not found"
            );
        }

        return notificationRepository.findByTaskTaskId(taskId);
    }

    @Override
    public Notification findNotificationById(long notificationId)
            throws ResourceNotFoundException {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "notification with id " + notificationId + " not found"));
    }

    @Override
    @Transactional
    public Notification addNotification(Notification notification)
            throws ResourceNotFoundException, IllegalArgumentException {

        if (notification == null) {
            throw new IllegalArgumentException("notification cannot be null");
        }
        if (notification.getNotificationId() != null) {
            throw new IllegalArgumentException("notification id must be null to create notification");
        }
        if (notification.getText() == null || notification.getText().isBlank()) {
            throw new IllegalArgumentException("notification text cannot be blank");
        }
        if (notification.getTask() == null || notification.getTask().getTaskId() == null) {
            throw new IllegalArgumentException("task association (with ID) is required for notification");
        }

        Task associatedTask = taskRepository.findById(notification.getTask().getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "cannot add notification. associated task with id " + notification.getTask().getTaskId() + " not found"
                ));

        notification.setTask(associatedTask);

        if (notification.getDate() == null) {
            notification.setDate(LocalDateTime.now());
        }

        try {
            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw  e;
        }
    }

    @Override
    @Transactional
    public void deleteNotification(long notificationId)
            throws ResourceNotFoundException {

        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException(
                    "delete error. notification with id " + notificationId + " not found");
        }

        notificationRepository.deleteById(notificationId);
    }

}

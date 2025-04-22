package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.InMemoryNotificationDAO;
import TGJavaProjects.TasksApplication.repository.InMemoryTaskDAO;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InMemoryNotificationImpl implements NotificationService {

    private final InMemoryUserServiceImpl userService;
    private final InMemoryTaskServiceImpl taskService;

    private final InMemoryTaskDAO taskDAO;
    private final InMemoryNotificationDAO notificationDAO;

    @Override
    public List<Notification> findAllNotifications() {
        return notificationDAO.findAllNotifications();
    }

    @Override
    public List<Notification> findNotificationsByUserId(long userId) {
        return List.of();
    }

    @Override
    public List<Notification> findNotificationsByTaskId(long taskId)
        throws ResourceNotFoundException {

        if (!taskDAO.existsById(taskId)) {
            throw new ResourceNotFoundException(
                    "cannot find notifications. task " + taskId + " not found"
            );
        }
        return notificationDAO.findNotificationsByTaskId(taskId);

    }

    @Override
    public Notification findNotificationById(long notificationId)
        throws ResourceNotFoundException {

        return notificationDAO.findNotificationById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "notification " + notificationId + " not found"));
    }

    @Override
    public Notification addNotification(Notification notification)
        throws ResourceNotFoundException, DuplicateResourceException, IllegalArgumentException {

        if (notification == null) {
            throw new IllegalArgumentException("notification cannot be null");
        }
//        if (notification.getNotificationId() == null) {
//            throw new IllegalArgumentException("notification id cannot be null");
//        }
//        if (notification.getTaskId() == null) {
//            throw new IllegalArgumentException("task id cannot be null for notification");
//        }
//        if (notification.getText() == null || notification.getText().isBlank()) {
//            throw new IllegalArgumentException("notification text cannot be null");
//        }

        if (!taskDAO.existsById(notification.getTaskId())) {
            throw new ResourceNotFoundException(
                    "cannot find notification. task " + notification.getTaskId() +
                            "not found"
            );
        }
        try {
            return notificationDAO.addNotification(notification);
        } catch (DuplicateResourceException | IllegalArgumentException e) {
            throw e;
        }

    }

    @Override
    public void deleteNotification(long notificationId)
        throws  ResourceNotFoundException, RuntimeException {

        if (!notificationDAO.existsById(notificationId)) {
            throw new ResourceNotFoundException(
                    "delete error. notification with id " + notificationId +
                            " not found");
        }
        boolean isDeleted = notificationDAO.deleteNotification(notificationId);
        if (! isDeleted) {
            throw new RuntimeException(
                    "delete failed for notification " + notificationId);
        }
    }


}
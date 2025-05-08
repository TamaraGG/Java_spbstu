package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    List<Notification> findAllNotifications();
    Notification saveNotification(Notification notification) throws DuplicateResourceException;
    Optional<Notification> findNotificationById(long notificationId);
    List<Notification> findNotificationsByUserId(long userId);
    boolean existsById(long notificationId);
    Notification updateNotification(Notification notification);
}

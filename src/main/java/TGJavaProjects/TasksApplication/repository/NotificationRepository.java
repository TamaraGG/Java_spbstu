package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    List<Notification> findAll();
    List<Notification> findNotificationsByUserId(long userId);

    Notification save(Notification notification);
    Optional<Notification> findById(Long notificationId);
    boolean existsById(Long notificationId);
}

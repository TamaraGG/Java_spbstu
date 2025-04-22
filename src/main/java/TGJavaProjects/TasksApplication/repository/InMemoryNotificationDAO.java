package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryNotificationDAO {

    private final List<Notification> notifications = new ArrayList<>();

    public List<Notification> findAllNotifications() {
        return List.copyOf(notifications);
    }

    public Optional<Notification> findNotificationById(long notificationId) {
        return notifications.stream()
                .filter(n -> n.getNotificationId().equals(notificationId))
                .findFirst();
    }

    public List<Notification> findNotificationsByTaskId(long taskId) {
        return notifications.stream()
                .filter(notification -> notification.getTaskId() == taskId)
                .toList();
    }

    public Notification addNotification(Notification notification)
        throws IllegalArgumentException, DuplicateResourceException {

        if (notification == null) {
            throw new IllegalArgumentException(
                    "notification cannot be null");
        }

        if (existsById(notification.getNotificationId())) {
            throw new DuplicateResourceException(
                    "notification id " + notification.getNotificationId()
                            + " already exists."
            );
        }
        notifications.add(notification);
        return notification;
    }

    public boolean deleteNotification(long notificationId) {
        return notifications.removeIf(
                t -> t.getNotificationId() == notificationId);
    }

    public boolean existsById(long notificationId) {
        return notifications.stream()
                .anyMatch(n -> n.getNotificationId().equals(notificationId));
    }

}

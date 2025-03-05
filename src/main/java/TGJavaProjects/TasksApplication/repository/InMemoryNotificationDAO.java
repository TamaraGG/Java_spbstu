package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryNotificationDAO {

    private final List<Notification> NOTIFICATIONS = new ArrayList<>();

    public List<Notification> getAllNotifications() {
        return NOTIFICATIONS;
    }

    public List<Notification> getUserNotifications(long userId) {
        return NOTIFICATIONS.stream()
                .filter(notification -> notification.getUserId() == userId)
                .toList();
    }

    public List<Notification> getTaskNotifications(long taskId) {
        return NOTIFICATIONS.stream()
                .filter(notification -> notification.getTaskId() == taskId)
                .toList();
    }
}

package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    public List<Notification> findAllNotifications();
    // List<Notification> findUserNotifications(long userId);
    public List<Notification> findNotificationsByTaskId(long taskId);
    public Notification findNotificationById(long notificationId);
    public Notification addNotification(Notification notification);
    public void deleteNotification(long notificationId);
}

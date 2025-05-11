package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    public List<Notification> findAllNotifications();
    public List<Notification> getAllNotificationsByUserId(long userId) throws ResourceNotFoundException;
    public List<Notification> getPendingNotificationsByUserId(long userId) throws ResourceNotFoundException;
    public Notification addNotification(Notification notification) throws ResourceNotFoundException;
    public void markNotificationAsRead(long userId, long notificationId) throws ResourceNotFoundException;
}
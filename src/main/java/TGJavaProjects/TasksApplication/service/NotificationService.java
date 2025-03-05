package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    public List<Notification> getAllNotifications();
    public List<Notification> getUserNotifications(long userId);
    public List<Notification> getTaskNotifications(long taskId);

}

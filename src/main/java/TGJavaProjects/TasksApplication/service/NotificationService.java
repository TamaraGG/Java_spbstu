package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NotificationService {

    public List<Notification> getAllNotifications();
    public Optional<List<Notification>> getUserNotifications(long userId);
    public Optional<List<Notification>> getTaskNotifications(long taskId);

}

package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NotificationService {

    public List<Notification> findAllNotifications();
    public Optional<List<Notification>> findUserNotifications(long userId);
    public Optional<List<Notification>> findTaskNotifications(long taskId);

}

package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.InMemoryNotificationDAO;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InMemoryNotificationImpl implements NotificationService {

    private final InMemoryNotificationDAO REPOSITORY;

    @Override
    public List<Notification> getAllNotifications() {
        return REPOSITORY.getAllNotifications();
    }

    @Override
    public List<Notification> getUserNotifications(long userId) {
        return REPOSITORY.getUserNotifications(userId);
    }

    @Override
    public List<Notification> getTaskNotifications(long taskId) {
        return REPOSITORY.getTaskNotifications(taskId);
    }
}

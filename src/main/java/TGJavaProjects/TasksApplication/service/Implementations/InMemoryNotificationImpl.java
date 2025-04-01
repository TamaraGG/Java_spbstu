package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.InMemoryNotificationDAO;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InMemoryNotificationImpl implements NotificationService {

    private final InMemoryUserServiceImpl userService;
    private final InMemoryTaskServiceImpl taskService;

    private final InMemoryNotificationDAO notificationDAO;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationDAO.getAllNotifications();
    }

    @Override
    public Optional<List<Notification>> getUserNotifications(long userId) {
        if (userService.findUserById(userId).isPresent()) {
            return Optional.of(notificationDAO.getUserNotifications(userId));
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Notification>> getTaskNotifications(long taskId) {
        if (taskService.getTaskById(taskId).isPresent()) {
            return Optional.of(notificationDAO.getTaskNotifications(taskId));
        }
        return Optional.empty();
    }
}
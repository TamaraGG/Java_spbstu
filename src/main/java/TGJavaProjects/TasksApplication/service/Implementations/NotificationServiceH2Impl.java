package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("H2")
public class NotificationServiceH2Impl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final TaskServiceH2Impl taskService;
    private final UserServiceH2Impl userService;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Optional<List<Notification>> getUserNotifications(long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public Optional<List<Notification>> getTaskNotifications(long taskId) {
        return notificationRepository.findByTaskId(taskId);
    }
}

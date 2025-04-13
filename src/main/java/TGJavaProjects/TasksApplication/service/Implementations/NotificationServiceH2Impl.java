package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Notification;
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
    @Override
    public List<Notification> getAllNotifications() {
        return List.of();
    }

    @Override
    public Optional<List<Notification>> getUserNotifications(long userId) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Notification>> getTaskNotifications(long taskId) {
        return Optional.empty();
    }
}

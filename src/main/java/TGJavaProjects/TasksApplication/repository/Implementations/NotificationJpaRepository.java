package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("h2")
public interface NotificationJpaRepository
        extends NotificationRepository, JpaRepository<Notification, Long> {
    @Override
    List<Notification> findNotificationsByUserId(long userId);
}

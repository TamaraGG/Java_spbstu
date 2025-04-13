package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationDAO extends JpaRepository<Notification, Long> {
}

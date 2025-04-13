package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDAO extends JpaRepository<Task, Long> {
    Optional<List<Task>> findByUserId (long userId);
}

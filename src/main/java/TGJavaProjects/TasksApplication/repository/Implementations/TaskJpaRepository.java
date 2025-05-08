package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("h2")
public interface TaskJpaRepository extends TaskRepository, JpaRepository<Task, Long> {
    List<Task> findByIsDeletedFalse();
    Optional<Task> findByTaskIdAndIsDeletedFalse(Long taskId);
    List<Task> findByUserIdAndIsDeletedFalse(Long userId);
    boolean existsByTaskIdAndIsDeletedFalse(Long taskId);
}
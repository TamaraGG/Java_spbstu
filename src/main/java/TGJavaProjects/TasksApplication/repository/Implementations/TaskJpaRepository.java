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
    @Override
    List<Task> findByUserIdAndIsDeletedFalse(Long userId);
    @Override
    List<Task> findByIsDeletedFalse();
    @Override
    Optional<Task> findByTaskIdAndIsDeletedFalse(Long taskId);
    @Override
    boolean existsByTaskIdAndIsDeletedFalse(Long taskId);
}
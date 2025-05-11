package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAll();
    Task save(Task task);
    Optional<Task> findById(Long taskId);
    boolean existsById(Long taskId);
    List<Task> findByUserIdAndIsDeletedFalse(Long userId);
    List<Task> findByIsDeletedFalse();
    Optional<Task> findByTaskIdAndIsDeletedFalse(Long taskId);
    boolean existsByTaskIdAndIsDeletedFalse(Long taskId);

    List<Task> findByIsDeletedFalseAndIsCompleteFalse();
}

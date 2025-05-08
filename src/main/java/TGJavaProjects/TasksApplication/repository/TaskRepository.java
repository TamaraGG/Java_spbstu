package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAllTasks();
    Task saveTask(Task task) throws DuplicateResourceException;
    Optional<Task> findTaskById(Long taskId);
    List<Task> findTasksByUserId(Long userId);
    boolean existsById(Long taskId);
    Task updateTask(Task task);
}

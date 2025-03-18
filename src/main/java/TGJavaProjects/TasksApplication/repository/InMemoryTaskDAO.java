package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryTaskDAO {

    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getAllTasks() {
        return List.copyOf(tasks);
    }

    public Optional<Task> findTaskById(Long taskId) {
        return tasks.stream()
                .filter(task -> task.getTaskId().equals(taskId))
                .findFirst();
    }

    public List<Task> findTasksByUserId(Long userId) {
        return tasks.stream()
                .filter(task -> task.getUserId().equals(userId))
                .toList();
    }

    public Task addTask(Task task)
            throws IllegalArgumentException, DuplicateResourceException {
        if (task == null) {
            throw new IllegalArgumentException(
                    "task cannot be null");
        }
        if (existsById(task.getTaskId())) {
            throw new DuplicateResourceException(
                    "task id " + task.getTaskId() + " already exists."
            );
        }
        tasks.add(task);
        return task;
    }

    public boolean deleteTask(long taskId) {
        return tasks.removeIf(t -> t.getTaskId() == taskId);
    }

    public boolean existsById(long taskId) {
        return tasks.stream()
                .anyMatch(t -> t.getTaskId().equals(taskId));
    }
}

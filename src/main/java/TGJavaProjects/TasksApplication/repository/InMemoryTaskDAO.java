package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryTaskDAO {
    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task getTaskById(long taskId) {
        return tasks.stream()
                .filter(task -> task.getTaskId() == taskId)
                .findFirst()
                .orElse(null);
    }

    public List<Task> getTasksByUserId(long userId) {
        return tasks.stream()
                .filter(task -> task.getUserId() == userId)
                .toList();
    }

    public Task addTask(Task task) {
        if (task != null && !existsById(task.getTaskId())) {
            tasks.add(task);
        }
        return task;
    }

    public Task deleteTask(long taskId) {
        var task = getTaskById(taskId);
        if (task != null) {
            tasks.remove(task);
        }
        return task;
    }

    public boolean existsById(long taskId) {
        return getTaskById(taskId) != null;
    }
}

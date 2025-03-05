package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryTaskDAO {
    private final List<Task> TASKS = new ArrayList<>();

    public List<Task> getAllTasks() {
        return TASKS;
    }

    public Task getTaskById(long taskId) {
        return TASKS.stream()
                .filter(task -> task.getTaskId() == taskId)
                .findFirst()
                .orElse(null);
    }

    public List<Task> getTasksByUserId(long userId) {
        return TASKS.stream()
                .filter(task -> task.getUserId() == userId)
                .toList();
    }

    public Task addTask(Task task) {
        TASKS.add(task);
        return task;
    }

    public Task deleteTask(long taskId) {
        var task = getTaskById(taskId);
        if (task != null) {
            TASKS.remove(task);
        }
        return task;
    }
}

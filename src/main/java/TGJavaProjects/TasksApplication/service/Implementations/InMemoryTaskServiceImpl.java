package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.InMemoryTaskDAO;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InMemoryTaskServiceImpl implements TaskService {

    private final InMemoryTaskDAO REPOSITORY;

    @Override
    public List<Task> getAllTasks() {
        return REPOSITORY.getAllTasks();
    }

    @Override
    public Optional<Task> getTaskById(long taskId) {
        return Optional.ofNullable(REPOSITORY.getTaskById(taskId));
    }

    @Override
    public List<Task> getTasksByUserId(long userId) {
        return REPOSITORY.getTasksByUserId(userId);
    }

    @Override
    public Optional<Task> addTask(Task task) {
        return Optional.ofNullable(REPOSITORY.addTask(task));
    }

    @Override
    public Optional<Task> deleteTask(long taskId) {
        return Optional.ofNullable(REPOSITORY.deleteTask(taskId));
    }
}

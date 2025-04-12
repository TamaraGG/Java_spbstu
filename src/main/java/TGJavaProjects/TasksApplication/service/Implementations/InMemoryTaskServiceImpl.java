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

    private final InMemoryUserServiceImpl userService;

    private final InMemoryTaskDAO taskDAO;

    @Override
    public List<Task> getAllTasks() {
        return taskDAO.getAllTasks();
    }

    @Override
    public Optional<Task> getTaskById(long taskId) {
        return Optional.ofNullable(taskDAO.getTaskById(taskId));
    }

    @Override
    public Optional<List<Task>> getTasksByUserId(long userId) {
        if (userService.findUserById(userId).isPresent()) {
            return Optional.of(taskDAO.getTasksByUserId(userId));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Task> addTask(Task task) {
        if (userService.findUserById(task.getUserId()).isPresent()) {
            return Optional.ofNullable(taskDAO.addTask(task));
        }
        else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Task> deleteTask(long taskId) {
        return Optional.ofNullable(taskDAO.deleteTask(taskId));
    }
}

package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.H2TaskDAO;
import TGJavaProjects.TasksApplication.repository.InMemoryTaskDAO;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Primary
public class H2TaskServiceImpl implements TaskService {

    private final InMemoryUserServiceImpl userService;

    private final H2TaskDAO taskDAO;

    @Override
    public List<Task> getAllTasks() {
        return List.of();
    }

    @Override
    public Optional<Task> getTaskById(long taskId) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Task>> getTasksByUserId(long userId) {
        return Optional.empty();
    }

    @Override
    public Optional<Task> addTask(Task task) {
        return Optional.empty();
    }

    @Override
    public Optional<Task> deleteTask(long taskId) {
        return Optional.empty();
    }
}

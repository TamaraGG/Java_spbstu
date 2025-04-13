package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskDAO;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("H2")
public class TaskServiceH2Impl implements TaskService {

    //private final InMemoryUserServiceImpl userService;

    private final TaskDAO taskDAO;

    @Override
    public List<Task> getAllTasks() {
        return taskDAO.findAll();
    }

    @Override
    public Optional<Task> getTaskById(long taskId) {
        return taskDAO.findById(taskId);
    }

    @Override
    public Optional<List<Task>> getTasksByUserId(long userId) {
        return taskDAO.findByUserId(userId);
    }

    @Override
    public Optional<Task> addTask(Task task) {
        return Optional.of(taskDAO.save(task));
    }

    @Override
    public Optional<Task> deleteTask(long taskId) {
        Optional<Task> deletedTask = taskDAO.findById(taskId);
        taskDAO.deleteById(taskId);
        return deletedTask;
    }
}

package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("H2")
public class TaskServiceH2Impl implements TaskService {

    private final UserServiceH2Impl userService;
    private final TaskRepository taskRepository;

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> getTaskById(long taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public Optional<List<Task>> getTasksByUserId(long userId) {
        return taskRepository.findByUserId(userId);
    }

    @Override
    public Optional<Task> addTask(@NotNull Task task) {
        if (userService.findUserById(task.getUserId()).isPresent()){
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Task> deleteTask(long taskId) {
        Optional<Task> deletedTask = taskRepository.findById(taskId);
        taskRepository.deleteById(taskId);
        return deletedTask;
    }
}


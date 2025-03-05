package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TaskService {

    public List<Task> getAllTasks();
    public Optional<Task> getTaskById(long taskId);
    public List<Task> getTasksByUserId(long userId);
    public Optional<Task> addTask (Task task);
    public Optional<Task> deleteTask(long taskId);
}
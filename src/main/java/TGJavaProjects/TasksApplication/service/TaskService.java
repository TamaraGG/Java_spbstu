package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskService {

    public List<Task> findAllTasks();
    public Task findTaskById(long taskId);
    public List<Task> findTasksByUserId(long userId);
    public Task addTask (Task task);
    public void deleteTask(long taskId);
}
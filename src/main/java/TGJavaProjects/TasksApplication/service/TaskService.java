package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskService {

    public List<Task> findAllTasks() throws ResourceNotFoundException;;
    public Task findTaskById(long userId, long taskId) throws ResourceNotFoundException;
    public List<Task> getAllTasksByUserId(long userId) throws ResourceNotFoundException;
    public List<Task> getPendingTasksByUserId(long userId) throws ResourceNotFoundException;
    public Task createTaskForUser(long userId, Task task) throws ResourceNotFoundException, DuplicateResourceException;
    public void softDeleteTask(long userId, long taskId) throws ResourceNotFoundException;
    public Task markTaskAsCompleted(long userId, long taskId) throws ResourceNotFoundException;
    public Task updateTaskDetails(long userId, long taskId, Task taskDetails) throws ResourceNotFoundException;
}
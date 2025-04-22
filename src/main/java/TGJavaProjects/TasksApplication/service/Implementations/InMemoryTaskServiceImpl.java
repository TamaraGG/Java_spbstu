package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.InMemoryTaskDAO;
import TGJavaProjects.TasksApplication.repository.InMemoryUserDAO;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InMemoryTaskServiceImpl implements TaskService {

    //private final InMemoryUserServiceImpl userService;

    private final InMemoryTaskDAO taskDAO;
    private final InMemoryUserDAO userDAO;

    @Override
    public List<Task> getAllTasks() {
        return taskDAO.getAllTasks();
    }

    @Override
    public Task findTaskById(long taskId) {
        return taskDAO.findTaskById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "task " + taskId + " not found"));
    }

    @Override
    public List<Task> findTasksByUserId(long userId) {
        if (!userDAO.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "cannot find tasks. user " + userId + " not found"
            );
        }
        return taskDAO.findTasksByUserId(userId);
    }

    @Override
    public Task addTask(Task task) {

        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        if (!userDAO.existsById(task.getUserId())) {
            throw new ResourceNotFoundException(
                    "cannot find task. user " + task.getUserId() +
                    "not found"
            );
        }
        try {
            return taskDAO.addTask(task);
        } catch (DuplicateResourceException | IllegalArgumentException e) {
            throw e;
        }
    }

    @Override
    public void deleteTask(long taskId) {
        if (!taskDAO.existsById(taskId)) {
            throw new ResourceNotFoundException(
                    "delete error. task with id " + taskId +
                            " not found");
        }
        boolean isDeleted = taskDAO.deleteTask(taskId);
        if (! isDeleted) {
            throw new RuntimeException(
                    "delete failed for task " + taskId);
        }
    }
    }
}

package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
import TGJavaProjects.TasksApplication.service.TaskService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InMemoryTaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private void checkUserExists(long userId) throws ResourceNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "user with id " + userId + " not found.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> findAllTasks() {
        return taskRepository.findByIsDeletedFalse();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "taskCache", key = "#taskId", unless = "#result == null")
    public Task findTaskById(long userId, long taskId) throws ResourceNotFoundException {
        checkUserExists(userId);
        return taskRepository.findByTaskIdAndIsDeletedFalse(taskId)
                .filter(task -> task.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "task with id " + taskId + " not found for user " + userId + " or it is marked as deleted."));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userTasksCache", key = "#userId")
    public List<Task> getAllTasksByUserId(long userId) throws ResourceNotFoundException {
        checkUserExists(userId);
        return taskRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userTasksCache", key = "'pending-' + #userId")
    public List<Task> getPendingTasksByUserId(long userId) throws ResourceNotFoundException {
        checkUserExists(userId);
        return taskRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .filter(task -> !task.getIsComplete())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "userTasksCache", key = "#userId"),
            @CacheEvict(cacheNames = "userTasksCache", key = "'pending-' + #userId")
    })
    public Task createTaskForUser(long userId, Task task)
            throws ResourceNotFoundException {
        checkUserExists(userId);

        if (task == null || task.getTaskText().isBlank()) {
            throw new IllegalArgumentException("task text cannot be empty.");
        }
        task.setUserId(userId);
        task.setIsComplete(false);
        task.setIsDeleted(false);

        Task createdTask = taskRepository.save(task);

        Notification notification = Notification.builder()
                .taskId(createdTask.getTaskId())
                .userId(userId)
                .text("New task created: " + createdTask.getTaskText())
                .build();
        notificationService.addNotification(notification);

        return createdTask;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "taskCache", key = "#taskId"),
            @CacheEvict(cacheNames = "userTasksCache", key = "#userId"),
            @CacheEvict(cacheNames = "userTasksCache", key = "'pending-' + #userId")
    })
    public void softDeleteTask(long userId, long taskId) throws ResourceNotFoundException {
        checkUserExists(userId);
        Task task = findTaskById(userId, taskId);
        task.setIsDeleted(true);
        taskRepository.save(task);
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = "taskCache", key = "#taskId"),
            evict = {
                    @CacheEvict(cacheNames = "userTasksCache", key = "#userId"),
                    @CacheEvict(cacheNames = "userTasksCache", key = "'pending-' + #userId")
            }
    )
    public Task markTaskAsCompleted(long userId, long taskId) throws ResourceNotFoundException {
        checkUserExists(userId);
        Task task = findTaskById(userId, taskId);
        if (task.getIsComplete()) {
            throw new IllegalStateException("task " + taskId + " is already completed.");
        }
        task.setIsComplete(true);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = "taskCache", key = "#taskId"),
            evict = {
                    @CacheEvict(cacheNames = "userTasksCache", key = "#userId"),
                    @CacheEvict(cacheNames = "userTasksCache", key = "'pending-' + #userId")
            }
    )
    public Task updateTaskDetails(long userId, long taskId, Task taskDetails) throws ResourceNotFoundException {
        checkUserExists(userId);
        Task existingTask = findTaskById(userId, taskId);

        if (taskDetails.getDueDate() != null) {
            existingTask.setDueDate(taskDetails.getDueDate());
        }
        if (!taskDetails.getTaskText().isBlank()) {
            existingTask.setTaskText(taskDetails.getTaskText());
        }

        return taskRepository.save(existingTask);
    }
}
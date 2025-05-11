package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.event.TaskCreatedEvent;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import TGJavaProjects.TasksApplication.service.AnalyticsService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryTaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(InMemoryTaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AnalyticsService analyticsService;

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
    @Cacheable(cacheNames = "taskCache", key = "#userId + '-' + #taskId", unless = "#result == null")
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
            throw new IllegalArgumentException("task text cannot be null or empty.");
        }
        task.setUserId(userId);
        task.setIsComplete(false);
        task.setIsDeleted(false);

        Task createdTask = taskRepository.save(task);
        log.debug("Task saved with ID: {}", createdTask.getTaskId());

        TaskCreatedEvent event = new TaskCreatedEvent(
                createdTask.getTaskId(),
                createdTask.getUserId(),
                createdTask.getTaskText()
        );

        try {
            eventPublisher.publishEvent(event);

            log.info("Published Spring event for task creation: {}", event);
        } catch (Exception e) {

            log.error("Error publishing Spring event for task creation: {}", event, e);

        }

        return createdTask;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "taskCache", key = "#userId + '-' + #taskId"),
            @CacheEvict(cacheNames = "userTasksCache", key = "#userId"),
            @CacheEvict(cacheNames = "userTasksCache", key = "'pending-' + #userId")
    })
    public void softDeleteTask(long userId, long taskId) throws ResourceNotFoundException {
        checkUserExists(userId);
        Task task = findTaskById(userId, taskId);
        task.setIsDeleted(true);
        taskRepository.save(task);
        log.info("Task with ID: {} for user ID: {} marked as deleted.", taskId, userId);
    }



    @Override
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = "taskCache", key = "#userId + '-' + #taskId"),
            evict = {
                    @CacheEvict(cacheNames = "userTasksCache", key = "#userId"),
                    @CacheEvict(cacheNames = "userTasksCache", key = "'pending-' + #userId")
            }
    )
    public Task markTaskAsCompleted(long userId, long taskId) throws ResourceNotFoundException {
        checkUserExists(userId);
        Task task = findTaskById(userId, taskId);
        if (task.getIsComplete()) {
            log.warn("Attempted to mark already completed task with ID: {} as completed.", taskId);
            throw new IllegalStateException("task " + taskId + " is already completed.");
        }
        task.setIsComplete(true);
        Task updatedTask = taskRepository.save(task);
        log.info("Task with ID: {} for user ID: {} marked as completed.", taskId, userId);

        analyticsService.recordTaskCompletionEvent(updatedTask);
        log.info("SYNC: Initiated asynchronous recording of task completion for taskId: {}", updatedTask.getTaskId());

        return updatedTask;
    }



    @Override
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = "taskCache", key = "#userId + '-' + #taskId"),
            evict = {
                    @CacheEvict(cacheNames = "userTasksCache", key = "#userId"),
                    @CacheEvict(cacheNames = "userTasksCache", key = "'pending-' + #userId")
            }
    )
    public Task updateTaskDetails(long userId, long taskId, Task taskDetails) throws ResourceNotFoundException {
        checkUserExists(userId);
        Task existingTask = findTaskById(userId, taskId);

        boolean updated = false;
        if (taskDetails.getDueDate() != null) {
            existingTask.setDueDate(taskDetails.getDueDate());
            updated = true;
        }
        if (!taskDetails.getTaskText().isBlank()) {
            existingTask.setTaskText(taskDetails.getTaskText());
            updated = true;
        }

        if (updated) {
            Task savedTask = taskRepository.save(existingTask);
            log.info("Task details updated for task ID: {} for user ID: {}", taskId, userId);
            return savedTask;
        } else {
            log.info("No details to update for task ID: {} for user ID: {}", taskId, userId);
            return existingTask;
        }
    }
}
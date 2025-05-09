package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.event.TaskCreatedEvent; // Import the new event
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
// NotificationService import is no longer needed here for addNotification
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.RequiredArgsConstructor; // Changed from AllArgsConstructor for selective injection
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate; // Import KafkaTemplate
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Use RequiredArgsConstructor for final fields
public class InMemoryTaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    // private final NotificationService notificationService; // Remove this direct dependency for addNotification
    private final KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate; // Inject KafkaTemplate

    @Value("${kafka.topic.task.created:task-creations-topic}") // Define topic name in properties or use default
    private String taskCreatedTopic;

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
            throw new IllegalArgumentException("task text cannot be empty.");
        }
        task.setUserId(userId);
        task.setIsComplete(false);
        task.setIsDeleted(false);

        Task createdTask = taskRepository.save(task);

        // Create an event DTO
        TaskCreatedEvent event = new TaskCreatedEvent(
                createdTask.getTaskId(),
                createdTask.getUserId(),
                createdTask.getTaskText()
        );

        // Send the event to Kafka
        try {
            kafkaTemplate.send(taskCreatedTopic, event);
            System.out.println("Sent task creation event to Kafka: " + event); // For debugging
        } catch (Exception e) {
            // Log error, handle exception (e.g., retry, DLQ - for simplicity, just logging now)
            System.err.println("Error sending task creation event to Kafka: " + e.getMessage());
            // Depending on requirements, you might re-throw or handle differently
        }

        // The direct call to notificationService.addNotification is removed.
        // Notification notification = Notification.builder()
        // .taskId(createdTask.getTaskId())
        // .userId(userId)
        // .text("New task created: " + createdTask.getTaskText())
        // .build();
        // notificationService.addNotification(notification);

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
            throw new IllegalStateException("task " + taskId + " is already completed.");
        }
        task.setIsComplete(true);
        return taskRepository.save(task);
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

        if (taskDetails.getDueDate() != null) {
            existingTask.setDueDate(taskDetails.getDueDate());
        }
        if (taskDetails.getTaskText() != null && !taskDetails.getTaskText().isBlank()) {
            existingTask.setTaskText(taskDetails.getTaskText());
        }

        return taskRepository.save(existingTask);
    }
}
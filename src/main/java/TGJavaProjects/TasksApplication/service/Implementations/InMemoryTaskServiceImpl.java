package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryTaskRepositoryImpl;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryUserRepositoryImpl;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Profile("InMemory")
public class InMemoryTaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private void checkUserExists(Long userId)
            throws ResourceNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "user with id " + userId + " not found.");
        }
    }

    @Override
    public List<Task> findAllTasks() {
        return taskRepository.findAllTasks();
    }

    @Override
    public Task findTaskById(long userId, long taskId)
            throws ResourceNotFoundException {

        checkUserExists(userId);
        return taskRepository.findTaskById(taskId)
                .filter(task -> task.getUserId().equals(userId) && !task.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "task with id " + taskId + " not found for user " + userId + " or it is marked as deleted."));
    }

    @Override
    public List<Task> getAllTasksByUserId(long userId)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        return taskRepository.findTasksByUserId(userId).stream()
                .filter(task -> !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPendingTasksByUserId(long userId)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        return taskRepository.findTasksByUserId(userId).stream()
                .filter(task -> !task.getIsComplete() && !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Task createTaskForUser(long userId, Task task)
            throws ResourceNotFoundException, DuplicateResourceException {
        checkUserExists(userId);

        if (task == null || task.getTaskText().isBlank()) {
            throw new IllegalArgumentException("task text cannot be empty.");
        }

        task.setIsComplete(false);
        task.setIsDeleted(false);

        Task createdTask = taskRepository.saveTask(task);

        Notification notification = Notification.builder()
                .taskId(createdTask.getTaskId())
                .userId(userId)
                .text("New task created: " + createdTask.getTaskText())
                .date(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationService.addNotification(notification);

        return createdTask;
    }

    @Override
    public void softDeleteTask(long userId, long taskId)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        Task task = findTaskById(userId, taskId);
        task.setIsDeleted(true);
        taskRepository.updateTask(task);
    }

    @Override
    public Task markTaskAsCompleted(long userId, long taskId)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        Task task = findTaskById(userId, taskId);
        if (task.getIsComplete()) {
            throw new IllegalStateException("task " + taskId + " is already completed.");
        }
        task.setIsComplete(true);
        return taskRepository.updateTask(task);
    }

    @Override
    public Task updateTaskDetails(long userId, long taskId, Task taskDetails)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        Task existingTask = findTaskById(userId, taskId);

        if (!taskDetails.getTaskText().isBlank()) {
            existingTask.setTaskText(taskDetails.getTaskText());
        }
        if (taskDetails.getDueDate() != null) {
            existingTask.setDueDate(taskDetails.getDueDate());
        }
        return taskRepository.updateTask(existingTask);
    }
}

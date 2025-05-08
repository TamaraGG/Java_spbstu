package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
import TGJavaProjects.TasksApplication.service.TaskService;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
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
    public Task findTaskById(long userId, long taskId) throws ResourceNotFoundException {
        checkUserExists(userId);
        return taskRepository.findByTaskIdAndIsDeletedFalse(taskId)
                .filter(task -> task.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "task with id " + taskId + " not found for user " + userId + " or it is marked as deleted."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasksByUserId(long userId) throws ResourceNotFoundException {
        checkUserExists(userId);
        return taskRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getPendingTasksByUserId(long userId) throws ResourceNotFoundException {
        checkUserExists(userId);
        return taskRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .filter(task -> !task.getIsComplete())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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
    public void softDeleteTask(long userId, long taskId) throws ResourceNotFoundException {
        checkUserExists(userId);
        Task task = findTaskById(userId, taskId);
        task.setIsDeleted(true);
        taskRepository.save(task);
    }

    @Override
    @Transactional
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

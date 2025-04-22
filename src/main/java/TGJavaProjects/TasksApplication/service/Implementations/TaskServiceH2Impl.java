package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("H2")
public class TaskServiceH2Impl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task findTaskById(long taskId)
            throws ResourceNotFoundException {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "task with id " + taskId + " not found"));
    }

    @Override
    public List<Task> findTasksByUserId(long userId)
            throws ResourceNotFoundException {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "cannot find tasks. user with id " + userId + " not found"
            );
        }

        return taskRepository.findByUserUserId(userId);
    }

    @Override
    @Transactional
    public Task addTask(Task task)
            throws IllegalArgumentException, ResourceNotFoundException {

        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }

        if (task.getTaskId() != null) {
            throw new IllegalArgumentException("task id must be null for new task creation");
        }

        if (task.getTaskText() == null || task.getTaskText().isBlank()) {
            throw new IllegalArgumentException("task text cannot be blank");
        }

        if (task.getUser() == null || task.getUser().getUserId() == null) {
            throw new IllegalArgumentException("user for task must exist");
        }

        User associatedUser = userRepository.findById(task.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "cannot add task. associated user with id " + task.getUser().getUserId() + " not found"
                ));

        task.setUser(associatedUser);

        if (task.getCreationDate() == null) {
            task.setCreationDate(LocalDateTime.now());
        }
        if (task.getIsComplete() == null) {
            task.setIsComplete(false);
        }

        try {
            return taskRepository.save(task);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteTask(long taskId)
            throws ResourceNotFoundException {

        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException(
                    "delete error. task with id " + taskId + " not found");
        }

        taskRepository.deleteById(taskId);
    }
}


package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("in-memory")
public class InMemoryTaskRepositoryImpl implements TaskRepository {
    private final List<Task> tasks = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public List<Task> findAllTasks() {
        return List.copyOf(tasks.stream()
                .filter(task -> !task.getIsDeleted()).collect(Collectors.toList()));
    }

    @Override
    public Optional<Task> findTaskById(Long taskId) {
        return tasks.stream()
                .filter(task -> task.getTaskId().equals(taskId)
                        && !task.getIsDeleted())
                .findFirst();
    }

    @Override
    public List<Task> findTasksByUserId(Long userId) {
        return tasks.stream()
                .filter(task -> task.getUserId().equals(userId)
                        && !task.getIsDeleted())
                .toList();
    }

    @Override
    public Task saveTask(Task task)
            throws IllegalArgumentException, DuplicateResourceException {
        if (task == null) {
            throw new IllegalArgumentException(
                    "task cannot be null");
        }

        if (task.getTaskId() == null) {
            task.setTaskId(idCounter.incrementAndGet());
            tasks.add(task);

        } else {
            boolean removed = tasks
                    .removeIf(t -> t.getTaskId().equals(task.getTaskId()));
            if (removed) {
                tasks.add(task);

            } else {
                throw new IllegalArgumentException(
                        "cannot update non-existing task with id " + task.getTaskId());
            }
        }
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null || task.getTaskId() == null) {
            throw new IllegalArgumentException(
                    "task or task id cannot be null for update");
        }
        Optional<Task> existingTaskOpt = tasks.stream()
                .filter(t -> t.getTaskId().equals(task.getTaskId()))
                .findFirst();

        if (existingTaskOpt.isPresent()) {
            tasks.removeIf(t -> t.getTaskId().equals(task.getTaskId()));
            tasks.add(task);
            return task;
        }
        throw new IllegalArgumentException(
                "task with id " + task.getTaskId() + " not found for update.");
    }

    @Override
    public boolean existsById(Long taskId) {
        return tasks.stream()
                .anyMatch(t -> t.getTaskId().equals(taskId)
                        && !t.getIsDeleted());
    }
}

package TGJavaProjects.TasksApplication.repository.Implementations;

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
    public List<Task> findAll() {
        return List.copyOf(tasks);
    }

    @Override
    public Optional<Task> findById(Long taskId) {
        if (taskId == null) return Optional.empty();
        return tasks.stream()
                .filter(task -> taskId.equals(task.getTaskId()))
                .findFirst();
    }

    @Override
    public List<Task> findByUserIdAndIsDeletedFalse(Long userId) {
        if (userId == null) return List.of();
        return tasks.stream()
                .filter(task -> userId.equals(task.getUserId()) && !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }

        if (task.getTaskId() == null) {
            task.setTaskId(idCounter.incrementAndGet());
            tasks.add(task);
        } else {
            int index = -1;
            for (int i = 0; i < tasks.size(); i++) {
                if (task.getTaskId().equals(tasks.get(i).getTaskId())) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                tasks.set(index, task);
            } else {
                throw new IllegalArgumentException(
                        "Task with id " + task.getTaskId() + " not found for update via save.");
            }
        }
        return task;
    }

    @Override
    public boolean existsById(Long taskId) {
        if (taskId == null) return false;
        return tasks.stream()
                .anyMatch(t -> taskId.equals(t.getTaskId()));
    }

    @Override
    public List<Task> findByIsDeletedFalse() {
        return tasks.stream()
                .filter(task -> !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Task> findByTaskIdAndIsDeletedFalse(Long taskId) {
        if (taskId == null) return Optional.empty();
        return tasks.stream()
                .filter(task -> taskId.equals(task.getTaskId()) && !task.getIsDeleted())
                .findFirst();
    }

    @Override
    public boolean existsByTaskIdAndIsDeletedFalse(Long taskId) {
        if (taskId == null) return false;
        return tasks.stream()
                .anyMatch(t -> taskId.equals(t.getTaskId()) && !t.getIsDeleted());
    }
}

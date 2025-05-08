package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
class TaskRequest {
    private String taskText;
    private LocalDateTime dueDate;
}

@RestController
@RequestMapping("/api/v1/users/{userId}/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllUserTasks(@PathVariable Long userId) {
        try {
            List<Task> tasks = taskService.getAllTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingUserTasks(@PathVariable Long userId) {
        try {
            List<Task> tasks = taskService.getPendingTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskByIdForUser(
            @PathVariable Long userId,
            @PathVariable Long taskId) {
        try {
            Task task = taskService.findTaskById(userId, taskId);
            return ResponseEntity.ok(task);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PostMapping
    public ResponseEntity<Task> createTaskForUser(@PathVariable Long userId, @RequestBody TaskRequest taskRequest) {
        try {
            if (taskRequest.getTaskText() == null || taskRequest.getTaskText().isBlank()) {
                throw new IllegalArgumentException("task text cannot be empty.");
            }
            Task taskToCreate = Task.builder()
                    .taskText(taskRequest.getTaskText())
                    .dueDate(taskRequest.getDueDate())
                    .build();

            Task createdTask = taskService.createTaskForUser(userId, taskToCreate);
            URI location = URI.create(String.format("/api/v1/users/%d/tasks/%d", userId, createdTask.getTaskId()));
            return ResponseEntity.created(location).body(createdTask);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (DuplicateResourceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> softDeleteTask(@PathVariable Long userId, @PathVariable Long taskId) {
        try {
            taskService.softDeleteTask(userId, taskId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<Task> markTaskAsCompleted(
            @PathVariable Long userId,
            @PathVariable Long taskId) {
        try {
            Task updatedTask = taskService.markTaskAsCompleted(userId, taskId);
            return ResponseEntity.ok(updatedTask);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTaskDetails(
            @PathVariable Long userId,
            @PathVariable Long taskId,
            @RequestBody TaskRequest taskRequest) {
        try {
            Task taskDetails = Task.builder()
                    .taskText(taskRequest.getTaskText())
                    .dueDate(taskRequest.getDueDate())
                    .build();
            Task updatedTask = taskService.updateTaskDetails(userId, taskId, taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }


}

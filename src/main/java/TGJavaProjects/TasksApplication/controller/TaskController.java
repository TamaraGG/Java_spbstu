package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks () {
        return taskService.findAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") long taskId)
        throws ResourceNotFoundException {

        try {
            Task task = taskService.findTaskById(taskId);
            return ResponseEntity.ok(task);
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUserId(@PathVariable long userId)
        throws ResourceNotFoundException {
        try {
            List<Task> tasks = taskService.findTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<Task> addTask (@RequestBody Task task)
        throws ResponseStatusException, DuplicateResourceException {
        try {
            Task createdTask = taskService.addTask(task);
            return ResponseEntity
                    .created(URI.create("/api/v1/tasks/" + createdTask.getTaskId()))
                    .body(createdTask);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e); // 400 Bad Request might be more appropriate than 404 here
        } catch (DuplicateResourceException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e); // 400 Bad Request
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable("id") long taskId)
        throws ResourceNotFoundException {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }


}

package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks () {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") long taskId) {
        return taskService.getTaskById(taskId)
                .map(t-> ResponseEntity.status(HttpStatus.OK).body(t))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUserId(@PathVariable long userId) {
        return taskService.getTasksByUserId(userId)
                .map(tasks -> new ResponseEntity<>(tasks, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> addTask (@RequestBody Task task) {
        return taskService.addTask(task)
                .map(t -> ResponseEntity.created(URI.create("/tasks/" + t.getTaskId())).body(t))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable("id") long taskId) {
        return taskService.deleteTask(taskId)
                .map(t-> new ResponseEntity<>(t, HttpStatus.OK))
                .orElse(ResponseEntity.badRequest().build());
    }


}

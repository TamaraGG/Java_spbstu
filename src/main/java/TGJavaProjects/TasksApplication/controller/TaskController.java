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

@Controller
@RequestMapping("/api/v1/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService TASKS_SERVICE;

    @GetMapping
    public List<Task> getAllTasks () {
        return TASKS_SERVICE.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") long taskId) {
        return TASKS_SERVICE.getTaskById(taskId)
                .map(t-> ResponseEntity.status(HttpStatus.OK).body(t))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{Id}")
    public List<Task> getTasksByUserId(@PathVariable("id") long userId) {
        return TASKS_SERVICE.getTasksByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Task> addTask (@RequestBody Task task) {
        return TASKS_SERVICE.addTask(task)
                .map(t -> ResponseEntity.created(URI.create("/tasks/" + t.getTaskId())).body(t))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable("id") long taskId) {
        return TASKS_SERVICE.deleteTask(taskId)
                .map(t-> new ResponseEntity<>(t, HttpStatus.OK))
                .orElse(ResponseEntity.badRequest().build());
    }


}

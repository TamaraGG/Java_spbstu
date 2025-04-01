package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService NOTIFICATION_SERVICE;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return NOTIFICATION_SERVICE.getAllNotifications();
    }


    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@RequestBody long userId) {
        return NOTIFICATION_SERVICE.getUserNotifications(userId);
    }

    @GetMapping("/{taskId}")
    public List<Notification> getTaskNotifications(@RequestBody long taskId) {
        return NOTIFICATION_SERVICE.getTaskNotifications(taskId);

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable long userId) {
        List<Notification> result = NOTIFICATION_SERVICE.getUserNotifications(userId);

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping("task/{taskId}")
    public ResponseEntity<List<Notification>> getTaskNotifications(@PathVariable long taskId) {
        List<Notification> result = NOTIFICATION_SERVICE.getTaskNotifications(taskId);

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

    }
}

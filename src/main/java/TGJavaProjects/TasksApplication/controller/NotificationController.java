package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> result = notificationService.getAllNotifications();

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable long userId) {
        return notificationService.getUserNotifications(userId)
                .map(notifications -> new ResponseEntity<>(notifications, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("task/{taskId}")
    public ResponseEntity<List<Notification>> getTaskNotifications(@PathVariable long taskId) {
        return notificationService.getTaskNotifications(taskId)
                .map(notifications -> new ResponseEntity<>(notifications, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

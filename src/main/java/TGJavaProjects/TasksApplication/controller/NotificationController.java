package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService NOTIFICATION_SERVICE;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> result = NOTIFICATION_SERVICE.getAllNotifications();

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable long userId) {
        Optional<List<Notification>> result = NOTIFICATION_SERVICE.getUserNotifications(userId);

        return result
                .map(notifications -> new ResponseEntity<>(notifications, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("task/{taskId}")
    public ResponseEntity<List<Notification>> getTaskNotifications(@PathVariable long taskId) {
        Optional<List<Notification>> result = NOTIFICATION_SERVICE.getTaskNotifications(taskId);

        return result
                .map(notifications -> new ResponseEntity<>(notifications, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

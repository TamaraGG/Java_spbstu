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
    public List<Notification> getAllNotifications() {
        return notificationService.findAllNotifications();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable long userId) {
        return notificationService.findUserNotifications(userId)
                .map(notifications -> new ResponseEntity<>(notifications, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("task/{taskId}")
    public ResponseEntity<List<Notification>> getTaskNotifications(@PathVariable long taskId) {
        return notificationService.findTaskNotifications(taskId)
                .map(notifications -> new ResponseEntity<>(notifications, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

        try {
            List<Notification> notifications = notificationService.findNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (ResourceNotFoundException e) {
            throw e;
        }

    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Notification>> getTaskNotifications(@PathVariable long taskId) {

        try {
            List<Notification> notifications = notificationService.findNotificationsByTaskId(taskId);
            return ResponseEntity.ok(notifications);
        } catch (ResourceNotFoundException e) {
            throw e;
        }

    }
}


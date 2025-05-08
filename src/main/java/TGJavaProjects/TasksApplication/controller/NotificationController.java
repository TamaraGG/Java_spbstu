package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users/{userId}/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllUserNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getAllNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPendingUserNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getPendingNotificationsByUserId(userId);
            return ResponseEntity.ok(notifications);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long userId,
            @PathVariable Long notificationId) {
        try {
            notificationService.markNotificationAsRead(userId, notificationId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}


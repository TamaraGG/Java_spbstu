package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
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
    }
}

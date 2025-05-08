package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryNotificationRepositoryImpl;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryTaskRepositoryImpl;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InMemoryNotificationImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private void checkUserExists(Long userId) throws ResourceNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "user with id " + userId + " not found.");
        }
    }

    @Override
    public List<Notification> findAllNotifications() {
        return notificationRepository.findAllNotifications();
    }

    @Override
    public List<Notification> getAllNotificationsByUserId(long userId)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        return notificationRepository.findNotificationsByUserId(userId);
    }

    @Override
    public List<Notification> getPendingNotificationsByUserId(long userId)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        return notificationRepository.findNotificationsByUserId(userId).stream()
                .filter(notification -> !notification.getIsRead())
                .collect(Collectors.toList());
    }

    @Override
    public Notification addNotification(Notification notification)
            throws ResourceNotFoundException {
        if (notification == null || notification.getUserId() == null || notification.getText() == null) {
            throw new IllegalArgumentException(
                    "notification, its userId, and text cannot be null.");
        }

        checkUserExists(notification.getUserId());
        try {
            return notificationRepository.saveNotification(notification);
        } catch (DuplicateResourceException e) {
            throw new RuntimeException(
                    "failed to save notification due to duplication: " + e.getMessage(), e);
        }
    }

    @Override
    public void markNotificationAsRead(long userId, long notificationId)
            throws ResourceNotFoundException {

        checkUserExists(userId);
        Notification notification = notificationRepository.findNotificationById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "notification with id " + notificationId + " not found for user " + userId));

        if (notification.getIsRead()) {
            return;
        }
        notification.setIsRead(true);
        notificationRepository.updateNotification(notification);
    }

}
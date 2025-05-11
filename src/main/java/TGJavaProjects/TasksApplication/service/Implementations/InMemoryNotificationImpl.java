package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
//@Profile("in-memory")
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
    @Transactional(readOnly = true)
    public List<Notification> findAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getAllNotificationsByUserId(long userId)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        return notificationRepository.findNotificationsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getPendingNotificationsByUserId(long userId)
            throws ResourceNotFoundException {
        checkUserExists(userId);
        return notificationRepository.findNotificationsByUserId(userId).stream()
                .filter(notification -> !notification.getIsRead())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Notification addNotification(Notification notification)
            throws ResourceNotFoundException {
        if (notification == null) {
            throw new IllegalArgumentException(
                    "notification cannot be null.");
        }

        checkUserExists(notification.getUserId());
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markNotificationAsRead(long userId, long notificationId)
            throws ResourceNotFoundException {

        checkUserExists(userId);
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "notification with id " + notificationId + " not found for user " + userId));

        if (notification.getIsRead()) {
            return;
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

}
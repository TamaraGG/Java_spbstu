package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryNotificationRepositoryImpl implements NotificationRepository {

    private final List<Notification> notifications = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public List<Notification> findAllNotifications() {
        return List.copyOf(notifications);
    }

    @Override
    public Optional<Notification> findNotificationById(long notificationId) {
        return notifications.stream()
                .filter(n -> n.getNotificationId().equals(notificationId))
                .findFirst();
    }

    @Override
    public List<Notification> findNotificationsByUserId(long userId) {
        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList()); // Изменено на collect
    }

    @Override
    public Notification saveNotification(Notification notification)
        throws IllegalArgumentException, DuplicateResourceException {

        if (notification.getNotificationId() == null) {
            notification.setNotificationId(idCounter.incrementAndGet());
            notifications.add(notification);
        } else {
            boolean removed = notifications
                    .removeIf(n -> n.getNotificationId().equals(notification.getNotificationId()));
            if (removed) {
                notifications.add(notification);
            } else {
                throw new IllegalArgumentException(
                        "cannot update non-existing notification with id " + notification.getNotificationId());
            }
        }
        return notification;
    }

    @Override
    public Notification updateNotification(Notification notification) {
        if (notification == null || notification.getNotificationId() == null) {
            throw new IllegalArgumentException(
                    "notification or notification id cannot be null for update");
        }
        Optional<Notification> existingOpt = notifications.stream()
                .filter(n -> n.getNotificationId().equals(notification.getNotificationId()))
                .findFirst();
        if (existingOpt.isPresent()) {
            notifications.removeIf(n -> n.getNotificationId().equals(notification.getNotificationId()));
            notifications.add(notification);
            return notification;
        }
        throw new IllegalArgumentException(
                "notification with id " + notification.getNotificationId() + " not found for update.");
    }

    @Override
    public boolean existsById(long notificationId) {
        return notifications.stream()
                .anyMatch(n -> n.getNotificationId().equals(notificationId));
    }

}

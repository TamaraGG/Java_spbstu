package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.repository.NotificationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("in-memory")
public class InMemoryNotificationRepositoryImpl implements NotificationRepository {

    private final List<Notification> notifications = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public List<Notification> findAll() {
        return List.copyOf(notifications);
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        if (notificationId == null) return Optional.empty();
        return notifications.stream()
                .filter(n -> notificationId.equals(n.getNotificationId()))
                .findFirst();
    }

    @Override
    public List<Notification> findNotificationsByUserId(long userId) {
        return notifications.stream()
                .filter(notification -> Long.valueOf(userId).equals(notification.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public Notification save(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("notification cannot be null");
        }

        if (notification.getNotificationId() == null) {
            notification.setNotificationId(idCounter.incrementAndGet());
            notifications.add(notification);
        } else {
            boolean removed = notifications
                    .removeIf(n -> notification.getNotificationId().equals(n.getNotificationId()));
            if (removed) {
                notifications.add(notification);
            } else {
                throw new IllegalArgumentException(
                        "Notification with id " + notification.getNotificationId() + " not found for update via save.");
            }
        }
        return notification;
    }

    @Override
    public boolean existsById(Long notificationId) {
        if (notificationId == null) return false;
        return notifications.stream()
                .anyMatch(n -> notificationId.equals(n.getNotificationId()));
    }

}

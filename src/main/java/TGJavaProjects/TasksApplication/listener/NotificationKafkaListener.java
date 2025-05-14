package TGJavaProjects.TasksApplication.listener;

import TGJavaProjects.TasksApplication.event.TaskCreatedEvent;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topic.task.created:task-creations-topic}",
            groupId = "${spring.kafka.consumer.group-id:task-notification-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTaskCreatedEvent(@Payload TaskCreatedEvent event) {
        log.info("Received task created event: {}", event);
        try {
            Notification notification = Notification.builder()
                    .taskId(event.getTaskId())
                    .userId(event.getUserId())
                    .text("New task created: " + event.getTaskText())
                    .build();

            notificationService.addNotification(notification);
            log.info("Notification created for task ID: {}", event.getTaskId());
        } catch (Exception e) {
            log.error("Error processing task created event for notification: {}", event, e);
        }
    }
}
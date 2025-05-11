package TGJavaProjects.TasksApplication.listener;

import TGJavaProjects.TasksApplication.event.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventKafkaProducerListener {

    private final KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

    @Value("${kafka.topic.task.created:task-creations-topic}")
    private String taskCreatedTopic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCreatedEventAndSendToKafka(TaskCreatedEvent event) {
        try {
            log.info("Transaction committed, sending task creation event to Kafka: {}", event);
            kafkaTemplate.send(taskCreatedTopic, event);
            log.info("Successfully sent task creation event to Kafka: {}", event);
        } catch (Exception e) {
            log.error("Error sending task creation event to Kafka after transaction commit: {}", event, e);
        }
    }
}
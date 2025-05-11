package TGJavaProjects.TasksApplication.listener;

import TGJavaProjects.TasksApplication.event.TaskCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskEventKafkaProducerListenerTest {

    @Mock
    private KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

    @InjectMocks
    private TaskEventKafkaProducerListener listener;

    @Captor
    private ArgumentCaptor<TaskCreatedEvent> eventArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> topicArgumentCaptor;

    private static final String TEST_TOPIC_NAME = "test-task-creations-topic";
    private TaskCreatedEvent testEvent;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "taskCreatedTopic", TEST_TOPIC_NAME);

        testEvent = new TaskCreatedEvent(1L, 10L, "Test Task Text");
    }

    @Test
    void handleTaskCreatedEventAndSendToKafka_Success() {
        listener.handleTaskCreatedEventAndSendToKafka(testEvent);

        verify(kafkaTemplate, times(1)).send(topicArgumentCaptor.capture(), eventArgumentCaptor.capture());

        assertEquals(TEST_TOPIC_NAME, topicArgumentCaptor.getValue());
        TaskCreatedEvent capturedEvent = eventArgumentCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals(testEvent.getTaskId(), capturedEvent.getTaskId());
        assertEquals(testEvent.getUserId(), capturedEvent.getUserId());
        assertEquals(testEvent.getTaskText(), capturedEvent.getTaskText());
    }

    @Test
    void handleTaskCreatedEventAndSendToKafka_KafkaSendThrowsException() {
        RuntimeException kafkaException = new RuntimeException("Kafka send failed");
        doThrow(kafkaException).when(kafkaTemplate).send(anyString(), any(TaskCreatedEvent.class));

        listener.handleTaskCreatedEventAndSendToKafka(testEvent);

        verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME), eq(testEvent));
    }

    @Test
    void handleTaskCreatedEventAndSendToKafka_EventIsNull() {
        listener.handleTaskCreatedEventAndSendToKafka(null);

        verify(kafkaTemplate, times(1)).send(eq(TEST_TOPIC_NAME),isNull());

    }
}
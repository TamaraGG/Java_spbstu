package TGJavaProjects.TasksApplication.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        TGJavaProjects.TasksApplication.config.kafka.KafkaProducerConfig.class,
        TGJavaProjects.TasksApplication.config.kafka.KafkaConsumerConfig.class,
        TGJavaProjects.TasksApplication.config.kafka.KafkaTopicConfig.class
})

@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.consumer.group-id=kafka-config-test-group",
        "kafka.topic.task.created=kafka-config-test-topic"
})
class KafkaConfigurationTest {

    @Autowired
    private KafkaTemplate<String, TGJavaProjects.TasksApplication.event.TaskCreatedEvent> kafkaTemplate;

    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String,
            TGJavaProjects.TasksApplication.event.TaskCreatedEvent> kafkaListenerContainerFactory;

    @Autowired
    private NewTopic taskCreatedTopic;

    @Test
    void contextLoadsAndKafkaBeansAreCreated() {
        assertNotNull(kafkaTemplate, "KafkaTemplate should be created");
        assertNotNull(kafkaListenerContainerFactory, "KafkaListenerContainerFactory should be created");
        assertNotNull(taskCreatedTopic, "NewTopic taskCreatedTopic should be created");
    }
}
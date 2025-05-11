package TGJavaProjects.TasksApplication.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KafkaTopicConfigTest {

    private KafkaTopicConfig kafkaTopicConfig;
    private final String testTopicName = "test-topic";

    @BeforeEach
    void setUp() {
        kafkaTopicConfig = new KafkaTopicConfig();
        ReflectionTestUtils.setField(kafkaTopicConfig, "taskCreatedTopicName", testTopicName);
    }

    @Test
    void taskCreatedTopic_createsNewTopicWithCorrectConfiguration() {
        NewTopic newTopic = kafkaTopicConfig.taskCreatedTopic();

        assertNotNull(newTopic);
        assertEquals(testTopicName, newTopic.name());
        assertEquals(1, newTopic.numPartitions());
        assertEquals(1, newTopic.replicasAssignments() == null ? newTopic.replicationFactor() : -1);
    }
}
package TGJavaProjects.TasksApplication.config.kafka;

import TGJavaProjects.TasksApplication.event.TaskCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConsumerConfigTest {

    private KafkaConsumerConfig kafkaConsumerConfig;
    private final String testBootstrapServers = "localhost:9092";
    private final String testGroupId = "test-group";

    @BeforeEach
    void setUp() {
        kafkaConsumerConfig = new KafkaConsumerConfig();
        ReflectionTestUtils.setField(kafkaConsumerConfig, "bootstrapServers", testBootstrapServers);
        ReflectionTestUtils.setField(kafkaConsumerConfig, "groupId", testGroupId);
    }

    @Test
    void taskCreatedEventConsumerFactory_createsFactoryWithCorrectProperties() {
        ConsumerFactory<String, TaskCreatedEvent> consumerFactory = kafkaConsumerConfig.taskCreatedEventConsumerFactory();

        assertNotNull(consumerFactory);
        assertTrue(consumerFactory instanceof DefaultKafkaConsumerFactory);

        Map<String, Object> props = consumerFactory.getConfigurationProperties();
        assertEquals(testBootstrapServers, props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(testGroupId, props.get(ConsumerConfig.GROUP_ID_CONFIG));

        DefaultKafkaConsumerFactory<?,?> defaultFactory = (DefaultKafkaConsumerFactory<?,?>) consumerFactory;
        assertTrue(defaultFactory.getKeyDeserializer() instanceof StringDeserializer, "Key deserializer should be StringDeserializer");
        assertTrue(defaultFactory.getValueDeserializer() instanceof JsonDeserializer, "Value deserializer should be JsonDeserializer");

        JsonDeserializer<?> valueDeserializer = (JsonDeserializer<?>) defaultFactory.getValueDeserializer();
    }

    @Test
    void kafkaListenerContainerFactory_createsFactoryAndSetsConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskCreatedEvent> factory =
                kafkaConsumerConfig.kafkaListenerContainerFactory();

        assertNotNull(factory);
        assertNotNull(factory.getConsumerFactory(), "ConsumerFactory should be set on KafkaListenerContainerFactory");
        assertTrue(factory.getConsumerFactory() instanceof DefaultKafkaConsumerFactory);
    }
}
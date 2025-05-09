package TGJavaProjects.TasksApplication.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.test.context.ActiveProfiles; // Можно убрать, если "test" не несет спец. настроек
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.test.context.TestPropertySource; // Для явного указания свойств

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        TGJavaProjects.TasksApplication.config.kafka.KafkaProducerConfig.class,
        TGJavaProjects.TasksApplication.config.kafka.KafkaConsumerConfig.class,
        TGJavaProjects.TasksApplication.config.kafka.KafkaTopicConfig.class
})
// @ActiveProfiles("test") // Можно убрать, если src/test/resources/application.properties достаточен
// Если вы убрали @ActiveProfiles("test"), то TestPropertySource будет применяться к конфигурации по умолчанию.
// Если оставили @ActiveProfiles("test"), то он будет применяться к конфигурации профиля "test".
// Для этого специфичного теста, который проверяет только Kafka бины, можно явно задать properties,
// чтобы он не зависел от внешних файлов application.properties.
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092", // Фиктивное значение для загрузки контекста
        "spring.kafka.consumer.group-id=kafka-config-test-group",
        "kafka.topic.task.created=kafka-config-test-topic"
        // Добавьте другие properties, если они нужны для @Value в ваших KafkaConfig классах
})
class KafkaConfigurationTest {

    @Autowired
    private KafkaTemplate<String, TGJavaProjects.TasksApplication.event.TaskCreatedEvent> kafkaTemplate;

    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String, TGJavaProjects.TasksApplication.event.TaskCreatedEvent> kafkaListenerContainerFactory;

    @Autowired
    private NewTopic taskCreatedTopic;

    @Test
    void contextLoadsAndKafkaBeansAreCreated() {
        assertNotNull(kafkaTemplate, "KafkaTemplate should be created");
        assertNotNull(kafkaListenerContainerFactory, "KafkaListenerContainerFactory should be created");
        assertNotNull(taskCreatedTopic, "NewTopic taskCreatedTopic should be created");
    }
}
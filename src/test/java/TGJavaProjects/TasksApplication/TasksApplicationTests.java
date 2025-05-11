package TGJavaProjects.TasksApplication;

import TGJavaProjects.TasksApplication.event.TaskCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("h2")
//@ActiveProfiles("in-memory")
@EnableScheduling
@EnableAsync
class TasksApplicationTests {

	@MockitoBean
	private KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

	@Test
	void contextLoads() {
	}
}

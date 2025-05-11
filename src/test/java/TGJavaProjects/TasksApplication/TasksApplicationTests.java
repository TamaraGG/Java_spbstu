package TGJavaProjects.TasksApplication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
//@ActiveProfiles("h2")
@ActiveProfiles("in-memory")
//@ActiveProfiles("pg")
class TasksApplicationTests {

	@Test
	void contextLoads() {
	}

}

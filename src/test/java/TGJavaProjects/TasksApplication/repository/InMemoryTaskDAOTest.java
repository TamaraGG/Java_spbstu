package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InMemoryTaskDAOTest {


    private InMemoryTaskDAO repository;

    private static final long USER_ID_1 = 10L;
    private static final long USER_ID_2 = 20L;
    private static final long TASK_ID_1 = 1L;
    private static final long TASK_ID_2 = 2L;
    private static final long NON_EXISTENT_ID = 999L;

    private static final Task TASK_1 = Task.builder()
            .taskId(TASK_ID_1)
            .userId(USER_ID_1)
            .taskText("Task 1 Text")
            .creationDate(LocalDateTime.now().minusDays(1))
            .dueDate(LocalDateTime.now().plusDays(5))
            .isComplete(false)
            .build();

    private static final Task TASK_2 = Task.builder()
            .taskId(TASK_ID_2)
            .userId(USER_ID_1)
            .taskText("Task 2 Text")
            .creationDate(LocalDateTime.now())
            .isComplete(true)
            .build();

    private static final Task TASK_3_OTHER_USER = Task.builder()
            .taskId(3L)
            .userId(USER_ID_2)
            .taskText("Task 3 Text - User 2")
            .creationDate(LocalDateTime.now())
            .isComplete(false)
            .build();

    private static final Task TASK_1_DUPLICATE_ID = Task.builder()
            .taskId(TASK_ID_1)
            .userId(USER_ID_2)
            .taskText("Duplicate Task")
            .creationDate(LocalDateTime.now())
            .isComplete(false)
            .build();

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskDAO();
    }

    @Test
    void getAllTasks_ReturnsListOfTasks_WhenNotEmpty() {
        repository.addTask(TASK_1);
        repository.addTask(TASK_3_OTHER_USER);

        List<Task> receivedTasks = repository.getAllTasks();

        assertNotNull(receivedTasks);
        assertEquals(2, receivedTasks.size());
        assertTrue(receivedTasks.contains(TASK_1));
        assertTrue(receivedTasks.contains(TASK_3_OTHER_USER));
    }

    @Test
    void getAllTasks_ReturnsEmptyList_WhenEmpty() {
        List<Task> receivedTasks = repository.getAllTasks();

        assertNotNull(receivedTasks);
        assertTrue(receivedTasks.isEmpty());
    }

    // findTaskById

    @Test
    void findTaskById_ReturnsOptionalWithTask_WhenExists() {
        repository.addTask(TASK_1);

        Optional<Task> foundTaskOpt = repository.findTaskById(TASK_ID_1);

        assertTrue(foundTaskOpt.isPresent());
        assertEquals(TASK_1, foundTaskOpt.get());
    }

    @Test
    void findTaskById_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.addTask(TASK_1);

        Optional<Task> foundTaskOpt = repository.findTaskById(NON_EXISTENT_ID);

        assertTrue(foundTaskOpt.isEmpty());
    }

    @Test
    void findTaskById_ReturnsEmptyOptional_WhenListIsEmpty() {
        Optional<Task> foundTaskOpt = repository.findTaskById(TASK_ID_1);
        assertTrue(foundTaskOpt.isEmpty());
    }

    // findTasksByUserId

    @Test
    void findTasksByUserId_ReturnsListOfUserTasks_WhenUserHasTasks() {
        repository.addTask(TASK_1);
        repository.addTask(TASK_2);
        repository.addTask(TASK_3_OTHER_USER);

        List<Task> user1Tasks = repository.findTasksByUserId(USER_ID_1);

        assertNotNull(user1Tasks);
        assertEquals(2, user1Tasks.size());
        assertTrue(user1Tasks.contains(TASK_1));
        assertTrue(user1Tasks.contains(TASK_2));
        assertFalse(user1Tasks.contains(TASK_3_OTHER_USER));
    }

    @Test
    void findTasksByUserId_ReturnsEmptyList_WhenUserHasNoTasks() {
        repository.addTask(TASK_1);

        List<Task> user2Tasks = repository.findTasksByUserId(USER_ID_2);

        assertNotNull(user2Tasks);
        assertTrue(user2Tasks.isEmpty());
    }

    @Test
    void findTasksByUserId_ReturnsEmptyList_WhenNoTasksExist() {
        List<Task> user1Tasks = repository.findTasksByUserId(USER_ID_1);

        assertNotNull(user1Tasks);
        assertTrue(user1Tasks.isEmpty());
    }

    // addTask

    @Test
    void addTask_ReturnsAddedTask_WhenValidAndUnique() {
        Task addedTask = repository.addTask(TASK_1);

        assertNotNull(addedTask);
        assertEquals(TASK_1, addedTask);
        assertEquals(1, repository.getAllTasks().size());
        assertTrue(repository.existsById(TASK_ID_1));
        assertEquals(Optional.of(TASK_1), repository.findTaskById(TASK_ID_1));
    }

    @Test
    void addTask_ThrowsDuplicateResourceException_WhenIdExists() {
        repository.addTask(TASK_1);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> repository.addTask(TASK_1_DUPLICATE_ID)
        );

        assertTrue(exception.getMessage().contains("task id " + TASK_ID_1 + " already exists"));
        assertEquals(1, repository.getAllTasks().size());
    }

    @Test
    void addTask_ThrowsIllegalArgumentException_WhenTaskIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.addTask(null)
        );
        assertEquals("task cannot be null", exception.getMessage());
        assertTrue(repository.getAllTasks().isEmpty());
    }

    // deleteTask

    @Test
    void deleteTask_ReturnsTrueAndRemovesTask_WhenExists() {
        repository.addTask(TASK_1);
        repository.addTask(TASK_2);
        assertEquals(2, repository.getAllTasks().size());

        boolean result = repository.deleteTask(TASK_ID_1);

        assertTrue(result);
        assertEquals(1, repository.getAllTasks().size());
        assertFalse(repository.existsById(TASK_ID_1));
        assertTrue(repository.existsById(TASK_ID_2));
    }

    @Test
    void deleteTask_ReturnsFalse_WhenDoesNotExist() {
        repository.addTask(TASK_1);
        assertEquals(1, repository.getAllTasks().size());

        boolean result = repository.deleteTask(NON_EXISTENT_ID);

        assertFalse(result);
        assertEquals(1, repository.getAllTasks().size());
        assertTrue(repository.existsById(TASK_ID_1));
    }

    // existsById

    @Test
    void existsById_ReturnsTrue_WhenExists() {
        repository.addTask(TASK_1);
        assertTrue(repository.existsById(TASK_ID_1));
    }

    @Test
    void existsById_ReturnsFalse_WhenDoesNotExist() {
        repository.addTask(TASK_1);
        assertFalse(repository.existsById(NON_EXISTENT_ID));
    }

    @Test
    void existsById_ReturnsFalse_WhenEmpty() {
        assertFalse(repository.existsById(TASK_ID_1));
    }

}
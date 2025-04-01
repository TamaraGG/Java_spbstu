package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InMemoryTaskDAOTest {

    private static final long USER_ID = 15L;
    private static final long TASK_ID = 1L;
    private static final Task task1 = mock(Task.class);
    private static final Task task2 = mock(Task.class);

    private InMemoryTaskDAO repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskDAO();
    }

    @Test
    void getAllTasks_ReturnsListOfTasks() {
        when(task1.getTaskId()).thenReturn(TASK_ID);
        when(task2.getTaskId()).thenReturn(TASK_ID + 1);
        repository.addTask(task1);
        repository.addTask(task2);

        List<Task> receivedTasks = repository.getAllTasks();

        assertNotNull(receivedTasks);
        assertEquals(2, receivedTasks.size());
    }

    @Test
    void getTasksByUserId_ReturnsListOfTasks() {
        // arrange
        when(task1.getUserId()).thenReturn(USER_ID);
        when(task2.getUserId()).thenReturn(USER_ID);
        when(task1.getTaskId()).thenReturn(TASK_ID);
        when(task2.getTaskId()).thenReturn(TASK_ID + 1);

        repository.addTask(task1);
        repository.addTask(task2);

        // act
        List<Task> receivedTasks = repository.getTasksByUserId(USER_ID);

        // assert
        assertNotNull(receivedTasks);
        assertEquals(2, receivedTasks.size());
    }

    @Test
    void getTaskById_ReturnsTask() {
        when(task1.getTaskId()).thenReturn(TASK_ID);
        repository.addTask(task1);

        Task recievedTask = repository.getTaskById(TASK_ID);

        assertNotNull(recievedTask);
        assertEquals(task1, recievedTask);
    }

    @Test
    void addTask_ReturnSavedTask() {
        // arrange
        when(task1.getTaskId()).thenReturn(TASK_ID);

        // act
        Task savedTask = repository.addTask(task1);

        // assert
        assertNotNull(savedTask);
        assertEquals(task1, savedTask);
    }

    @Test
    void InMemoryTaskDAO_DeleteTask_ReturnDeletedTask() {
        when(task1.getTaskId()).thenReturn(TASK_ID);

        repository.addTask(task1);
        Task deletedTask = repository.deleteTask(task1.getTaskId());

        assertNotNull(deletedTask);
        assertEquals(task1, deletedTask);
    }
}
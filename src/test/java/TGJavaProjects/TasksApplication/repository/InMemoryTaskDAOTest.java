package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InMemoryTaskDAOTest {


    private InMemoryTaskDAO repository;

    private static final Task task1 = mock(Task.class);
    private static final Task task2 = mock(Task.class);

    private static final long userId = 15L;
    private static final long taskId = 1L;


    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskDAO();
    }

    @Test
    void getAllTasks_ReturnsListOfTasks_WhenNotEmpty() {
        when(task1.getTaskId()).thenReturn(taskId);
        when(task2.getTaskId()).thenReturn(taskId + 1);
        repository.addTask(task1);
        repository.addTask(task2);

        List<Task> receivedTasks = repository.getAllTasks();

        assertNotNull(receivedTasks);
        assertEquals(2, receivedTasks.size());
    }

    @Test
    void getAllTasks_ReturnsListOfTasks_WhenEmpty() {

        List<Task> receivedTasks = repository.getAllTasks();

        assertNotNull(receivedTasks);
        assertEquals(0, receivedTasks.size());
    }

    @Test
    void getTasksByUserId_ReturnsListOfUserTasks_WhenNotEmpty() {

        when(task1.getUserId()).thenReturn(userId);
        when(task2.getUserId()).thenReturn(userId + 1);
        repository.addTask(task1);
        repository.addTask(task2);

        List<Task> receivedTasks = repository.getTasksByUserId(userId);

        assertNotNull(receivedTasks);
        assertEquals(1, receivedTasks.size());
    }

    @Test
    void getTasksByUserId_ReturnsListOfUserTasks_WhenEmpty() {

        List<Task> receivedTasks = repository.getTasksByUserId(userId);

        assertNotNull(receivedTasks);
        assertEquals(0, receivedTasks.size());
    }

    @Test
    void getTaskById_ReturnsTask_WhenExists() {
        when(task1.getTaskId()).thenReturn(taskId);
        repository.addTask(task1);

        Task recievedTask = repository.getTaskById(taskId);

        assertNotNull(recievedTask);
        assertEquals(task1, recievedTask);
    }

    @Test
    void getTaskById_ReturnsTask_WhenDoesNotExist() {
        when(task1.getTaskId()).thenReturn(taskId);
        repository.addTask(task1);

        Task recievedTask = repository.getTaskById(taskId + 1);

        assertNull(recievedTask);
    }

    @Test
    void addTask_ReturnsAddedTask_WhenNotNull() {
        when(task1.getTaskId()).thenReturn(taskId);

        Task savedTask = repository.addTask(task1);

        assertNotNull(savedTask);
        assertEquals(task1, savedTask);
        assertEquals(1, repository.getAllTasks().size());
    }

    @Test
    void addTask_ReturnsAddedTask_WhenNull() {

        Task savedTask = repository.addTask(null);

        assertNull(savedTask);
        assertEquals(0, repository.getAllTasks().size());
    }

    @Test
    void deleteTask_ReturnsDeletedTask_WhenTaskExists() {
        when(task1.getTaskId()).thenReturn(taskId);
        when(task2.getTaskId()).thenReturn(taskId + 1);
        repository.addTask(task1);
        repository.addTask(task2);

        Task deletedTask = repository.deleteTask(taskId);

        assertNotNull(deletedTask);
        assertEquals(task1, deletedTask);
        assertEquals( 1, repository.getAllTasks().size());
    }

    @Test
    void deleteTask_ReturnsDeletedTask_WhenTaskDoesNotExist() {
        when(task1.getTaskId()).thenReturn(taskId);
        repository.addTask(task1);

        Task deletedTask = repository.deleteTask(taskId + 1);

        assertNull(deletedTask);
        assertEquals(1, repository.getAllTasks().size());
    }
}
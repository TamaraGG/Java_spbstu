package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private static final long USER_ID = 15L;
    private static final long TASK_ID = 1L;
    private static final Task task1 = mock(Task.class);
    private static final Task task2 = mock(Task.class);

    @Autowired
    private InMemoryTaskDAO repository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getAllTasks() {
        when(task1.getTaskId()).thenReturn(TASK_ID);
        when(task2.getTaskId()).thenReturn(TASK_ID + 1);
        repository.addTask(task1);
        repository.addTask(task2);

        List<Task> receivedTasks = repository.getAllTasks();

        assertNotNull(receivedTasks);
        assertEquals(2, receivedTasks.size());
    }

    @Test
    void InMemoryTaskDAO_GetTasksByUserId_ReturnListOfTasks() {
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
    void InMemoryTaskDAO_GetTaskById_ReturnTask() {
        when(task1.getTaskId()).thenReturn(TASK_ID);
        repository.addTask(task1);

        Task recievedTask = repository.getTaskById(TASK_ID);

        assertNotNull(recievedTask);
        assertEquals(task1, recievedTask);
    }

    @Test
    void InMemoryTaskDAO_AddTask_ReturnSavedTask() {
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
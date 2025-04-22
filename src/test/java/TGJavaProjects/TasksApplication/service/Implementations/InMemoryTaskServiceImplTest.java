package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.InMemoryNotificationDAO;
import TGJavaProjects.TasksApplication.repository.InMemoryTaskDAO;
import TGJavaProjects.TasksApplication.repository.InMemoryUserDAO;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryTaskServiceImplTest {

    @InjectMocks
    private InMemoryTaskServiceImpl taskService;

    @Mock
    private InMemoryUserDAO userDAO;
    @Mock
    private InMemoryTaskDAO taskDAO;

    private Task task1;
    private Task task2;
    private static final long USER_ID_1 = 1L;
    private static final long TASK_ID_1 = 10L;
    private static final long TASK_ID_2 = 20L;
    private static final long NON_EXISTENT_TASK_ID = 4L;
    private static final long NON_EXISTENT_USER_ID = 3L;

    @BeforeEach
    void setUp() {
        task1 = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText("Test Task 1")
                .creationDate(LocalDateTime.now().minusDays(1))
                .isComplete(false)
                .build();

        task2 = Task.builder()
                .taskId(TASK_ID_2)
                .userId(USER_ID_1)
                .taskText("Test Task 2")
                .creationDate(LocalDateTime.now())
                .isComplete(true)
                .build();
    }

// findAllTasks

    @Test
    void findAllTasks_ReturnsListOfTasks() {
        List<Task> expectedTasks = List.of(task1, task2);
        when(taskDAO.getAllTasks()).thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.findAllTasks();

        assertNotNull(actualTasks);
        assertEquals(expectedTasks, actualTasks);
        assertEquals(2, actualTasks.size());
        verify(taskDAO, times(1)).getAllTasks();
    }

    @Test
    void findAllTasks_ReturnsEmptyList() {
        when(taskDAO.getAllTasks()).thenReturn(Collections.emptyList());

        List<Task> actualTasks = taskService.findAllTasks();

        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());
        verify(taskDAO, times(1)).getAllTasks();
    }

    // findTaskById

    @Test
    void findTaskById_ReturnsTask_WhenFound() {
        when(taskDAO.findTaskById(TASK_ID_1)).thenReturn(Optional.of(task1));

        Task foundTask = taskService.findTaskById(TASK_ID_1);

        assertNotNull(foundTask);
        assertEquals(task1, foundTask);
        verify(taskDAO, times(1)).findTaskById(TASK_ID_1);
    }

    @Test
    void findTaskById_ThrowsNotFound_WhenNotFound() {
        when(taskDAO.findTaskById(NON_EXISTENT_TASK_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTaskById(NON_EXISTENT_TASK_ID)
        );

        assertTrue(exception.getMessage().contains("task " + NON_EXISTENT_TASK_ID + " not found"));
        verify(taskDAO, times(1)).findTaskById(NON_EXISTENT_TASK_ID);
    }

    // findTasksByUserId

    @Test
    void findTasksByUserId_ReturnsTasks_WhenUserExists() {
        List<Task> expectedTasks = List.of(task1, task2);
        when(userDAO.existsById(USER_ID_1)).thenReturn(true);
        when(taskDAO.findTasksByUserId(USER_ID_1)).thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.findTasksByUserId(USER_ID_1);

        assertNotNull(actualTasks);
        assertEquals(expectedTasks, actualTasks);
        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(taskDAO, times(1)).findTasksByUserId(USER_ID_1);
    }

    @Test
    void findTasksByUserId_ReturnsEmptyList_WhenUserExistsButNoTasks() {
        when(userDAO.existsById(USER_ID_1)).thenReturn(true);
        when(taskDAO.findTasksByUserId(USER_ID_1)).thenReturn(Collections.emptyList());

        List<Task> actualTasks = taskService.findTasksByUserId(USER_ID_1);

        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());
        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(taskDAO, times(1)).findTasksByUserId(USER_ID_1);
    }

    @Test
    void findTasksByUserId_ThrowsNotFound_WhenUserDoesNotExist() {
        when(userDAO.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTasksByUserId(NON_EXISTENT_USER_ID)
        );

        assertTrue(exception.getMessage().contains("cannot find tasks. user " + NON_EXISTENT_USER_ID + " not found"));
        verify(userDAO, times(1)).existsById(NON_EXISTENT_USER_ID);
        verify(taskDAO, never()).findTasksByUserId(anyLong());
    }

    // addTask

    @Test
    void addTask_ReturnsTask_WhenValidAndUserExists() {
        when(userDAO.existsById(USER_ID_1)).thenReturn(true);
        when(taskDAO.addTask(any(Task.class))).thenReturn(task1);

        Task addedTask = taskService.addTask(task1);

        assertNotNull(addedTask);
        assertEquals(task1, addedTask);
        assertNotNull(task1.getCreationDate());
        assertNotNull(task1.getIsComplete());
        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(taskDAO, times(1)).addTask(task1);
    }


    @Test
    void addTask_ThrowsNotFound_WhenUserDoesNotExist() {
        when(userDAO.existsById(USER_ID_1)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.addTask(task1)
        );
        assertTrue(exception.getMessage().contains("cannot find task. user " + USER_ID_1 + "not found"));
        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(taskDAO, never()).addTask(any(Task.class));
    }

    @Test
    void addTask_ThrowsDuplicateException_WhenDaoThrows() {

        when(userDAO.existsById(USER_ID_1)).thenReturn(true);
        when(taskDAO.addTask(task1)).thenThrow(new DuplicateResourceException("ID exists"));

        assertThrows(DuplicateResourceException.class, () -> taskService.addTask(task1));
        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(taskDAO, times(1)).addTask(task1);
    }

    @Test
    void addTask_ThrowsIllegalArgument_WhenTaskIsNull() {
        assertThrows(IllegalArgumentException.class, () -> taskService.addTask(null));
        verify(userDAO, never()).existsById(anyLong());
        verify(taskDAO, never()).addTask(any(Task.class));
    }

    // deleteTask

    @Test
    void deleteTask_CompletesNormally_WhenSuccessful() {
        when(taskDAO.existsById(TASK_ID_1)).thenReturn(true);
        when(taskDAO.deleteTask(TASK_ID_1)).thenReturn(true);

        assertDoesNotThrow(() -> taskService.deleteTask(TASK_ID_1));

        verify(taskDAO, times(1)).existsById(TASK_ID_1);
        verify(taskDAO, times(1)).deleteTask(TASK_ID_1);
    }

    @Test
    void deleteTask_ThrowsNotFound_WhenTaskDoesNotExist() {
        when(taskDAO.existsById(NON_EXISTENT_TASK_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.deleteTask(NON_EXISTENT_TASK_ID)
        );
        assertTrue(exception.getMessage().contains("delete error. task with id " + NON_EXISTENT_TASK_ID + " not found"));
        verify(taskDAO, times(1)).existsById(NON_EXISTENT_TASK_ID);
        verify(taskDAO, never()).deleteTask(anyLong());
    }

    @Test
    void deleteTask_ThrowsRuntimeException_WhenDaoDeleteFails() {
        when(taskDAO.existsById(TASK_ID_1)).thenReturn(true);
        when(taskDAO.deleteTask(TASK_ID_1)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.deleteTask(TASK_ID_1)
        );
        assertTrue(exception.getMessage().contains("delete failed for task " + TASK_ID_1));
        verify(taskDAO, times(1)).existsById(TASK_ID_1);
        verify(taskDAO, times(1)).deleteTask(TASK_ID_1);
    }

}


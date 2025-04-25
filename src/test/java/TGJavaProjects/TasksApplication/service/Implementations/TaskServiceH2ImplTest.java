package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Profile("!inmemory")
class TaskServiceH2ImplTest {

    @InjectMocks
    private TaskServiceH2Impl taskService;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    private Task task1;
    private Task task2;
    private Task taskToCreate;
    private User user1;

    private static final long USER_ID_1 = 1L;
    private static final long TASK_ID_1 = 10L;
    private static final long TASK_ID_2 = 20L;
    private static final long NON_EXISTENT_TASK_ID = 4L;
    private static final long NON_EXISTENT_USER_ID = 3L;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userId(USER_ID_1)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .build();

        taskToCreate = Task.builder()
                .user(user1)
                .taskText("New Task Text")

                .build();

        task1 = Task.builder()
                .taskId(TASK_ID_1)
                .user(user1)
                .taskText("Test Task 1")
                .creationDate(LocalDateTime.now().minusDays(1))
                .isComplete(false)
                .build();

        task2 = Task.builder()
                .taskId(TASK_ID_2)
                .user(user1)
                .taskText("Test Task 2")
                .creationDate(LocalDateTime.now())
                .isComplete(true)
                .build();
    }

    // findAllTasks
    @Test
    void findAllTasks_ShouldReturnListOfTasks() {
        List<Task> expectedTasks = List.of(task1, task2);
        when(taskRepository.findAll()).thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.findAllTasks();

        assertNotNull(actualTasks);
        assertEquals(expectedTasks, actualTasks);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void findAllTasks_ShouldReturnEmptyList_WhenNoTasksExist() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<Task> actualTasks = taskService.findAllTasks();

        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());
        verify(taskRepository, times(1)).findAll();
    }

    // findTaskById
    @Test
    void findTaskById_ShouldReturnTask_WhenTaskExists() {
        when(taskRepository.findById(TASK_ID_1)).thenReturn(Optional.of(task1));

        Task foundTask = taskService.findTaskById(TASK_ID_1);

        assertNotNull(foundTask);
        assertEquals(task1, foundTask);
        verify(taskRepository, times(1)).findById(TASK_ID_1);
    }

    @Test
    void findTaskById_ShouldThrowResourceNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(NON_EXISTENT_TASK_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTaskById(NON_EXISTENT_TASK_ID)
        );
        assertTrue(exception.getMessage().contains("task with id " + NON_EXISTENT_TASK_ID + " not found"));
        verify(taskRepository, times(1)).findById(NON_EXISTENT_TASK_ID);
    }

    // findTasksByUserId
    @Test
    void findTasksByUserId_ShouldReturnTasks_WhenUserExists() {
        List<Task> expectedTasks = List.of(task1, task2);
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByUserUserId(USER_ID_1)).thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.findTasksByUserId(USER_ID_1);

        assertNotNull(actualTasks);
        assertEquals(expectedTasks, actualTasks);
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(taskRepository, times(1)).findByUserUserId(USER_ID_1);
    }

    @Test
    void findTasksByUserId_ShouldReturnEmptyList_WhenUserExistsButHasNoTasks() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByUserUserId(USER_ID_1)).thenReturn(Collections.emptyList());

        List<Task> actualTasks = taskService.findTasksByUserId(USER_ID_1);

        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(taskRepository, times(1)).findByUserUserId(USER_ID_1);
    }

    @Test
    void findTasksByUserId_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTasksByUserId(NON_EXISTENT_USER_ID)
        );
        assertTrue(exception.getMessage().contains("cannot find tasks. user with id " + NON_EXISTENT_USER_ID + " not found"));
        verify(userRepository, times(1)).existsById(NON_EXISTENT_USER_ID);
        verify(taskRepository, never()).findByUserUserId(anyLong());
    }

    // addTask
    @Test
    void addTask_ShouldReturnSavedTask_WhenValidAndUserExists() {
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user1));

        Task savedTask = Task.builder()
                .taskId(TASK_ID_1)
                .user(user1)
                .taskText(taskToCreate.getTaskText())
                .creationDate(LocalDateTime.now())
                .isComplete(false)
                .build();
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.addTask(taskToCreate);

        assertNotNull(result);
        assertEquals(TASK_ID_1, result.getTaskId());
        assertEquals(user1, result.getUser());
        assertNotNull(result.getCreationDate());
        assertFalse(result.getIsComplete());
        verify(userRepository, times(1)).findById(USER_ID_1);
        verify(taskRepository, times(1)).save(taskToCreate);
    }

    @Test
    void addTask_ShouldThrowIllegalArgumentException_WhenTaskIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addTask(null)
        );
        assertEquals("task cannot be null", exception.getMessage());
        verifyNoInteractions(userRepository, taskRepository);
    }

    @Test
    void addTask_ShouldThrowIllegalArgumentException_WhenTaskIdIsNotNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addTask(task1)
        );
        assertEquals("task id must be null for new task creation", exception.getMessage());
        verifyNoInteractions(userRepository, taskRepository);
    }

    @Test
    void addTask_ShouldThrowIllegalArgumentException_WhenTaskTextIsBlank() {
        taskToCreate.setTaskText(" ");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addTask(taskToCreate)
        );
        assertEquals("task text cannot be blank", exception.getMessage());
        verifyNoInteractions(userRepository, taskRepository);
    }

    @Test
    void addTask_ShouldThrowIllegalArgumentException_WhenUserIsNullInTask() {
        taskToCreate.setUser(null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addTask(taskToCreate)
        );
        assertEquals("user for task must exist", exception.getMessage());
        verifyNoInteractions(userRepository, taskRepository);
    }

    @Test
    void addTask_ShouldThrowIllegalArgumentException_WhenUserIdIsNullInTask() {
        taskToCreate.getUser().setUserId(null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addTask(taskToCreate)
        );
        assertEquals("user for task must exist", exception.getMessage());
        verifyNoInteractions(userRepository, taskRepository);
    }


    @Test
    void addTask_ShouldThrowResourceNotFoundException_WhenAssociatedUserNotFound() {
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.addTask(taskToCreate)
        );
        assertTrue(exception.getMessage().contains("cannot add task. associated user with id " + USER_ID_1 + " not found"));
        verify(userRepository, times(1)).findById(USER_ID_1);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void addTask_ShouldRethrowException_WhenRepositorySaveFails() {
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user1));
        when(taskRepository.save(any(Task.class))).thenThrow(new RuntimeException("Simulated save error"));

        assertThrows(RuntimeException.class, () -> taskService.addTask(taskToCreate));
        verify(userRepository, times(1)).findById(USER_ID_1);
        verify(taskRepository, times(1)).save(taskToCreate);
    }

    // deleteTask
    @Test
    void deleteTask_ShouldCompleteSuccessfully_WhenTaskExists() {
        when(taskRepository.existsById(TASK_ID_1)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(TASK_ID_1);

        assertDoesNotThrow(() -> taskService.deleteTask(TASK_ID_1));

        verify(taskRepository, times(1)).existsById(TASK_ID_1);
        verify(taskRepository, times(1)).deleteById(TASK_ID_1);
    }

    @Test
    void deleteTask_ShouldThrowResourceNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(NON_EXISTENT_TASK_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.deleteTask(NON_EXISTENT_TASK_ID)
        );
        assertTrue(exception.getMessage().contains("delete error. task with id " + NON_EXISTENT_TASK_ID + " not found"));
        verify(taskRepository, times(1)).existsById(NON_EXISTENT_TASK_ID);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteTask_ShouldHandleException_WhenRepositoryDeleteThrows() {
        when(taskRepository.existsById(TASK_ID_1)).thenReturn(true);
        doThrow(new RuntimeException("Simulated delete error")).when(taskRepository).deleteById(TASK_ID_1);

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(TASK_ID_1));

        verify(taskRepository, times(1)).existsById(TASK_ID_1);
        verify(taskRepository, times(1)).deleteById(TASK_ID_1);
    }
}
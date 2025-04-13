package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.InMemoryNotificationDAO;
import TGJavaProjects.TasksApplication.repository.InMemoryTaskDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryTaskServiceImplTest {

    @InjectMocks
    private InMemoryTaskServiceImpl taskService;

    @Mock
    private InMemoryUserServiceImpl userService;
    @Mock
    private InMemoryTaskDAO taskDAO;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {

        user = new User(
                1L,
                "Jane",
                "Doe",
                "test@gmail.com"
        );

        task = new Task(
                1L, "test task",
                LocalDateTime.now(), LocalDateTime.now(), true, 1L
        );
    }


    @Test
    void getAllTasks_ReturnsListOfTasks_WhenNotEmpty() {

        when(taskDAO.getAllTasks())
                .thenReturn(List.of(task));

        List<Task> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        verify(taskDAO, times(1)).getAllTasks();
    }

    @Test
    void getAllTasks_ReturnsListOfTasks_WhenEmpty() {

        when(taskDAO.getAllTasks())
                .thenReturn(List.of());

        List<Task> result = taskService.getAllTasks();

        assertEquals(0, result.size());
        verify(taskDAO, times(1)).getAllTasks();

    }

    @Test
    void getTaskById_ReturnsTask_WhenTaskExists() {

        when(taskDAO.getTaskById(task.getTaskId()))
                .thenReturn(task);

        Optional<Task> result = taskService.getTaskById(task.getTaskId());

        assert(result.isPresent());
        assertEquals(task, result.get());
        verify(taskDAO, times(1)).getTaskById(task.getTaskId());

    }

    @Test
    void getTaskById_ReturnsTask_WhenTaskDoesNotExist() {

        when(taskDAO.getTaskById(task.getTaskId()))
                .thenReturn(null);

        Optional<Task> result = taskService.getTaskById(task.getTaskId());

        assert(result.isEmpty());
        verify(taskDAO, times(1)).getTaskById(task.getTaskId());

    }

    @Test
    void getTasksByUserId_ReturnsListOfTasks_WhenUserExists() {

        when(userService.findUserById(task.getUserId()))
                .thenReturn(Optional.ofNullable(user));
        when(taskDAO.getTasksByUserId(task.getUserId()))
                .thenReturn(List.of(task));

        Optional<List<Task>> result = taskService.getTasksByUserId(task.getUserId());

        assert(result.isPresent());
        assertEquals(1, result.get().size());
        verify(taskDAO, times(1)).getTasksByUserId(task.getUserId());

    }

    @Test
    void getTasksByUserId_ReturnsListOfTasks_WhenUserDoesNotExist() {

        when(userService.findUserById(task.getUserId()))
                .thenReturn(Optional.empty());

        Optional<List<Task>> result = taskService.getTasksByUserId(task.getUserId());

        assert(result.isEmpty());
        verify(taskDAO, times(0)).getTasksByUserId(task.getUserId());

    }

    @Test
    void addTask_ReturnsTask_WhenUserExists() {

        when(userService.findUserById(task.getUserId()))
                .thenReturn(Optional.ofNullable(user));
        when(taskDAO.addTask(task))
                .thenReturn(task);

        Optional<Task> result = taskService.addTask(task);

        assert(result.isPresent());
        assertEquals(task, result.get());
        verify(taskDAO, times(1)).addTask(task);

    }

    @Test
    void addTask_ReturnsTask_WhenUserDoesNotExist() {

        when(userService.findUserById(task.getUserId()))
                .thenReturn(Optional.empty());

        Optional<Task> result = taskService.addTask(task);

        assert(result.isEmpty());
        verify(taskDAO, times(0)).addTask(task);

    }

    @Test
    void deleteTask_ReturnDeletedTask_WhenTaskExists() {

        when(taskDAO.deleteTask(task.getTaskId()))
                .thenReturn(task);

        Optional<Task> result = taskService.deleteTask(task.getTaskId());

        assert(result.isPresent());
        assertEquals(task, result.get());
        verify(taskDAO, times(1)).deleteTask(task.getTaskId());

    }

    @Test
    void deleteTask_ReturnDeletedTask_WhenTaskDoesNotExist() {

        when(taskDAO.deleteTask(task.getTaskId()))
                .thenReturn(null);

        Optional<Task> result = taskService.deleteTask(task.getTaskId());

        assert(result.isEmpty());
        verify(taskDAO, times(1)).deleteTask(task.getTaskId());

    }
}
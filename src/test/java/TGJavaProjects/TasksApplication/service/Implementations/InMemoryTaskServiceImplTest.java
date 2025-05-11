package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
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
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    private Task task1, task2, task1Completed, task1Deleted;
    private Task taskToCreate;
    private Notification createdNotification;

    private static final long USER_ID_1 = 1L;
    private static final long TASK_ID_1 = 10L;
    private static final long TASK_ID_2 = 20L;
    private static final long NON_EXISTENT_USER_ID = 99L;
    private static final long NON_EXISTENT_TASK_ID = 999L;
    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        taskToCreate = Task.builder()
                .userId(USER_ID_1)
                .taskText("New Task Text")
                .dueDate(NOW.plusDays(3))
                .build();

        task1 = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText("Task 1 Text")
                .creationDate(NOW.minusDays(1))
                .dueDate(NOW.plusDays(5))
                .isComplete(false)
                .isDeleted(false)
                .build();

        task2 = Task.builder()
                .taskId(TASK_ID_2)
                .userId(USER_ID_1)
                .taskText("Task 2 Text")
                .creationDate(NOW)
                .dueDate(NOW.plusDays(2))
                .isComplete(true)
                .isDeleted(false)
                .build();

        task1Completed = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText(task1.getTaskText())
                .creationDate(task1.getCreationDate())
                .dueDate(task1.getDueDate())
                .isComplete(true)
                .isDeleted(false)
                .build();

        task1Deleted = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText(task1.getTaskText())
                .creationDate(task1.getCreationDate())
                .dueDate(task1.getDueDate())
                .isComplete(false)
                .isDeleted(true)
                .build();

        createdNotification = Notification.builder()
                .userId(USER_ID_1)
                .text("New task created: " + taskToCreate.getTaskText())
                .taskId(TASK_ID_1)
                .date(NOW)
                .isRead(false)
                .build();
    }

    // findAllTasks

    @Test
    void findAllTasks_ReturnsListOfTasksFromRepository() {
        List<Task> expectedTasks = List.of(task1, task2);
        when(taskRepository.findAllTasks()).thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.findAllTasks();

        assertEquals(expectedTasks, actualTasks);
        verify(taskRepository, times(1)).findAllTasks();
    }

    // findTaskById

    @Test
    void findTaskById_ReturnsTask_WhenUserAndTaskExistAndTaskBelongsToUserAndNotDeleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(TASK_ID_1))
                .thenReturn(Optional.of(task1));

        Task foundTask = taskService.findTaskById(USER_ID_1, TASK_ID_1);

        assertNotNull(foundTask);
        assertEquals(task1, foundTask);
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(taskRepository, times(1)).findTaskById(TASK_ID_1);
    }

    @Test
    void findTaskById_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTaskById(NON_EXISTENT_USER_ID, TASK_ID_1)
        );
        assertTrue(exception.getMessage()
                .contains("user with id " + NON_EXISTENT_USER_ID + " not found."));
        verify(userRepository, times(1))
                .existsById(NON_EXISTENT_USER_ID);
        verify(taskRepository, never()).findTaskById(anyLong());
    }

    @Test
    void findTaskById_ThrowsResourceNotFound_WhenTaskDoesNotExist() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(NON_EXISTENT_TASK_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTaskById(USER_ID_1, NON_EXISTENT_TASK_ID)
        );
        assertTrue(exception.getMessage()
                .contains("task with id " + NON_EXISTENT_TASK_ID + " not found for user " + USER_ID_1));
        verify(userRepository, times(1))
                .existsById(USER_ID_1);
        verify(taskRepository, times(1))
                .findTaskById(NON_EXISTENT_TASK_ID);
    }

    @Test
    void findTaskById_ThrowsResourceNotFound_WhenTaskIsDeleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(TASK_ID_1)).thenReturn(Optional.of(task1Deleted));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTaskById(USER_ID_1, TASK_ID_1)
        );
        assertTrue(exception.getMessage()
                .contains("task with id " + TASK_ID_1 + " not found for user " + USER_ID_1));
        verify(taskRepository, times(1)).findTaskById(TASK_ID_1);
    }

    @Test
    void findTaskById_ThrowsResourceNotFound_WhenTaskDoesNotBelongToUser() {
        Task taskOfAnotherUser = Task.builder()
                .taskId(TASK_ID_1)
                .userId(999L)
                .taskText(task1.getTaskText())
                .isDeleted(false).build();
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(TASK_ID_1)).thenReturn(Optional.of(taskOfAnotherUser));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTaskById(USER_ID_1, TASK_ID_1)
        );
        assertTrue(exception.getMessage()
                .contains("task with id " + TASK_ID_1 + " not found for user " + USER_ID_1));
        verify(taskRepository, times(1)).findTaskById(TASK_ID_1);
    }


    // getAllTasksByUserId

    @Test
    void getAllTasksByUserId_ReturnsNonDeletedTasksForUser() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        List<Task> tasksFromRepo = List.of(task1, task2, task1Deleted);
        when(taskRepository.findTasksByUserId(USER_ID_1)).thenReturn(tasksFromRepo);

        List<Task> result = taskService.getAllTasksByUserId(USER_ID_1);

        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
        assertFalse(result.contains(task1Deleted));
        verify(taskRepository, times(1)).findTasksByUserId(USER_ID_1);
    }

    @Test
    void getAllTasksByUserId_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> taskService.getAllTasksByUserId(NON_EXISTENT_USER_ID));
        verify(taskRepository, never()).findTasksByUserId(anyLong());
    }

    // getPendingTasksByUserId

    @Test
    void getPendingTasksByUserId_ReturnsOnlyPendingAndNonDeletedTasks() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        List<Task> tasksFromRepo = List.of(task1, task2, task1Deleted);
        when(taskRepository.findTasksByUserId(USER_ID_1)).thenReturn(tasksFromRepo);

        List<Task> result = taskService.getPendingTasksByUserId(USER_ID_1);

        assertEquals(1, result.size());
        assertTrue(result.contains(task1));
        assertFalse(result.contains(task2));
        assertFalse(result.contains(task1Deleted));
        verify(taskRepository, times(1)).findTasksByUserId(USER_ID_1);
    }

    @Test
    void getPendingTasksByUserId_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getPendingTasksByUserId(NON_EXISTENT_USER_ID));
        verify(taskRepository, never()).findTasksByUserId(anyLong());
    }

    // createTaskForUser

    @Test
    void createTaskForUser_CreatesTaskAndNotification_WhenValid() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        Task savedTaskWithId = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText(taskToCreate.getTaskText())
                .dueDate(taskToCreate.getDueDate())
                .creationDate(NOW)
                .isComplete(false)
                .isDeleted(false)
                .build();
        when(taskRepository.saveTask(any(Task.class))).thenReturn(savedTaskWithId);

        when(notificationService.addNotification(any(Notification.class)))
                .thenReturn(mock(Notification.class));


        Task result = taskService.createTaskForUser(USER_ID_1, taskToCreate);

        assertNotNull(result);
        assertEquals(savedTaskWithId.getTaskId(), result.getTaskId());
        assertEquals(USER_ID_1, result.getUserId());
        assertFalse(result.getIsComplete());
        assertFalse(result.getIsDeleted());
        assertNotNull(result.getCreationDate());

        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(taskRepository, times(1)).saveTask(argThat(task ->
                task.getUserId().equals(USER_ID_1) &&
                        task.getTaskText().equals(taskToCreate.getTaskText()) &&
                        !task.getIsComplete() &&
                        !task.getIsDeleted()
        ));

        verify(notificationService, times(1))
                .addNotification(argThat(notification ->
                        notification.getUserId().equals(USER_ID_1) &&
                                notification.getText().contains(savedTaskWithId.getTaskText()) &&
                                !notification.getIsRead()
        ));
    }

    @Test
    void createTaskForUser_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> taskService.createTaskForUser(NON_EXISTENT_USER_ID, taskToCreate));
        verify(taskRepository, never()).saveTask(any());
        verify(notificationService, never()).addNotification(any());
    }


    @Test
    void createTaskForUser_ThrowsDuplicateResourceException_WhenRepositoryThrows() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.saveTask(any(Task.class))).thenThrow(new DuplicateResourceException("..."));

        assertThrows(DuplicateResourceException.class,
                () -> taskService.createTaskForUser(USER_ID_1, taskToCreate));
        verify(notificationService, never()).addNotification(any());
    }

    @Test
    void createTaskForUser_ThrowsIllegalArgumentException_WhenTaskIsNull() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTaskForUser(USER_ID_1, null)
        );
        assertEquals("task text cannot be empty.", exception.getMessage());
        verify(taskRepository, never()).saveTask(any());
        verify(notificationService, never()).addNotification(any());
    }

    @Test
    void createTaskForUser_ThrowsIllegalArgumentException_WhenTaskTextIsBlank() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        Task taskWithBlankText = Task.builder()
                .userId(USER_ID_1)
                .taskText("   ")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTaskForUser(USER_ID_1, taskWithBlankText)
        );

        assertEquals("task text cannot be empty.", exception.getMessage());
        verify(taskRepository, never()).saveTask(any());
        verify(notificationService, never()).addNotification(any());
    }

    @Test
    void createTaskForUser_ThrowsIllegalArgumentException_WhenTaskTextIsEmpty() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        Task taskWithEmptyText = Task.builder()
                .userId(USER_ID_1)
                .taskText("")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTaskForUser(USER_ID_1, taskWithEmptyText)
        );
        assertEquals("task text cannot be empty.", exception.getMessage());
        verify(taskRepository, never()).saveTask(any());
        verify(notificationService, never()).addNotification(any());
    }

    // softDeleteTask

    @Test
    void softDeleteTask_MarksTaskAsDeleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(TASK_ID_1)).thenReturn(Optional.of(task1));
        when(taskRepository.updateTask(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        taskService.softDeleteTask(USER_ID_1, TASK_ID_1);

        verify(taskRepository, times(1)).updateTask(argThat(task ->
                task.getTaskId().equals(TASK_ID_1) &&
                        task.getIsDeleted()
        ));
    }

    @Test
    void softDeleteTask_ThrowsResourceNotFound_WhenTaskNotFound() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(NON_EXISTENT_TASK_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.softDeleteTask(USER_ID_1, NON_EXISTENT_TASK_ID));
        verify(taskRepository, never()).updateTask(any());
    }


    // markTaskAsCompleted

    @Test
    void markTaskAsCompleted_MarksTaskAsComplete() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(TASK_ID_1)).thenReturn(Optional.of(task1));
        when(taskRepository.updateTask(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.markTaskAsCompleted(USER_ID_1, TASK_ID_1);

        assertNotNull(result);
        assertTrue(result.getIsComplete());
        verify(taskRepository, times(1)).updateTask(argThat(task ->
                task.getTaskId().equals(TASK_ID_1) &&
                        task.getIsComplete()
        ));
    }

    @Test
    void markTaskAsCompleted_ThrowsIllegalState_WhenTaskAlreadyCompleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(TASK_ID_2)).thenReturn(Optional.of(task2));

        assertThrows(IllegalStateException.class,
                () -> taskService.markTaskAsCompleted(USER_ID_1, TASK_ID_2));
        verify(taskRepository, never()).updateTask(any());
    }


    // updateTaskDetails

    @Test
    void updateTaskDetails_UpdatesTextAndDueDate() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findTaskById(TASK_ID_1)).thenReturn(Optional.of(task1));
        when(taskRepository.updateTask(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task detailsToUpdate = Task.builder()
                .taskText("Updated Details Text")
                .dueDate(NOW.plusDays(10))
                .taskId(TASK_ID_1)
                .userId(USER_ID_1).build();

        Task result = taskService.updateTaskDetails(USER_ID_1, TASK_ID_1, detailsToUpdate);

        assertNotNull(result);
        assertEquals("Updated Details Text", result.getTaskText());
        assertEquals(detailsToUpdate.getDueDate(), result.getDueDate());

        assertEquals(task1.getUserId(), result.getUserId());
        assertEquals(task1.getIsComplete(), result.getIsComplete());
        assertEquals(task1.getIsDeleted(), result.getIsDeleted());
        assertEquals(task1.getTaskId(), result.getTaskId());

        verify(taskRepository, times(1)).updateTask(argThat(task ->
                task.getTaskId().equals(TASK_ID_1) &&
                        task.getTaskText().equals("Updated Details Text") &&
                        task.getDueDate().equals(detailsToUpdate.getDueDate())
        ));
    }

    @Test
    void updateTaskDetails_OnlyUpdatesProvidedFields() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        Task originalTask = Task.builder()
                .taskId(task1.getTaskId())
                .userId(task1.getUserId())
                .taskText(task1.getTaskText())
                .creationDate(task1.getCreationDate())
                .dueDate(task1.getDueDate())
                .isComplete(task1.getIsComplete())
                .isDeleted(task1.getIsDeleted())
                .build();
        when(taskRepository.findTaskById(TASK_ID_1)).thenReturn(Optional.of(originalTask));
        when(taskRepository.updateTask(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task detailsWithOnlyText = Task.builder()
                .taskText(task1.getTaskText())
                .taskId(TASK_ID_1)
                .taskText("Only Text Updated")
                .userId(USER_ID_1).build();

        Task result1 = taskService.updateTaskDetails(USER_ID_1, TASK_ID_1, detailsWithOnlyText);
        assertEquals("Only Text Updated", result1.getTaskText());
        assertEquals(originalTask.getDueDate(), result1.getDueDate());

        Task taskAfterTextUpdate = Task.builder()
                .taskId(task1.getTaskId())
                .userId(task1.getUserId())
                .taskText("Only Text Updated")
                .creationDate(task1.getCreationDate())
                .dueDate(task1.getDueDate())
                .isComplete(task1.getIsComplete())
                .isDeleted(task1.getIsDeleted())
                .build();
        when(taskRepository.findTaskById(TASK_ID_1)).thenReturn(Optional.of(taskAfterTextUpdate));

        Task detailsWithOnlyDueDate = Task.builder()
                .dueDate(NOW.plusMonths(1))
                .userId(USER_ID_1)
                .taskText(taskAfterTextUpdate.getTaskText())
                .taskId(TASK_ID_1).build();

        Task result2 = taskService.updateTaskDetails(USER_ID_1, TASK_ID_1, detailsWithOnlyDueDate);

        assertEquals("Only Text Updated", result2.getTaskText());
        assertEquals(detailsWithOnlyDueDate.getDueDate(), result2.getDueDate());
    }

}


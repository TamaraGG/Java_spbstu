package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Profile("inmemory")
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

    private static final long USER_ID_1 = 1L;
    private static final long TASK_ID_1 = 10L;
    private static final long TASK_ID_2 = 20L;
    private static final long NON_EXISTENT_USER_ID = 99L;
    private static final long NON_EXISTENT_TASK_ID = 999L;
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String TASK_TEXT_1 = "Task 1 Text";
    private static final String TASK_TEXT_NEW = "New Task Text";


    @BeforeEach
    void setUp() {
        taskToCreate = Task.builder()
                .userId(USER_ID_1)
                .taskText(TASK_TEXT_NEW)
                .dueDate(NOW.plusDays(3))
                .build();

        task1 = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText(TASK_TEXT_1)
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
                .userId(task1.getUserId())
                .taskText(task1.getTaskText())
                .creationDate(task1.getCreationDate())
                .dueDate(task1.getDueDate())
                .isComplete(true)
                .isDeleted(task1.getIsDeleted())
                .build();

        task1Deleted = Task.builder()
                .taskId(TASK_ID_1)
                .userId(task1.getUserId())
                .taskText(task1.getTaskText())
                .creationDate(task1.getCreationDate())
                .dueDate(task1.getDueDate())
                .isComplete(task1.getIsComplete())
                .isDeleted(true)
                .build();
    }

    // findAllTasks

    @Test
    void findAllTasks_ReturnsListOfNonDeletedTasks() {
        List<Task> expectedTasks = List.of(task1, task2);
        when(taskRepository.findByIsDeletedFalse()).thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.findAllTasks();

        assertEquals(expectedTasks, actualTasks);
        verify(taskRepository, times(1)).findByIsDeletedFalse();
    }

    // findTaskById

    @Test
    void findTaskById_ReturnsTask_WhenUserAndTaskExistAndTaskBelongsToUserAndNotDeleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByTaskIdAndIsDeletedFalse(TASK_ID_1)).thenReturn(Optional.of(task1));

        Task foundTask = taskService.findTaskById(USER_ID_1, TASK_ID_1);

        assertNotNull(foundTask);
        assertEquals(task1, foundTask);
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(taskRepository, times(1)).findByTaskIdAndIsDeletedFalse(TASK_ID_1);
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
        verify(taskRepository, never()).findByTaskIdAndIsDeletedFalse(anyLong());
    }

    @Test
    void findTaskById_ThrowsResourceNotFound_WhenTaskDoesNotExistOrDeleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByTaskIdAndIsDeletedFalse(NON_EXISTENT_TASK_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTaskById(USER_ID_1, NON_EXISTENT_TASK_ID)
        );
        assertTrue(exception.getMessage()
                .contains("task with id " + NON_EXISTENT_TASK_ID + " not found for user " + USER_ID_1));
        verify(userRepository, times(1))
                .existsById(USER_ID_1);
        verify(taskRepository, times(1))
                .findByTaskIdAndIsDeletedFalse(NON_EXISTENT_TASK_ID);
    }

    @Test
    void findTaskById_ThrowsResourceNotFound_WhenTaskDoesNotBelongToUser() {
        Task taskOfAnotherUser = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1 + 3)
                .taskText(task1.getTaskText())
                .isDeleted(false).build();
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByTaskIdAndIsDeletedFalse(TASK_ID_1)).thenReturn(Optional.of(taskOfAnotherUser));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.findTaskById(USER_ID_1, TASK_ID_1)
        );
        assertTrue(exception.getMessage()
                .contains("task with id " + TASK_ID_1 + " not found for user " + USER_ID_1));
        verify(taskRepository, times(1)).findByTaskIdAndIsDeletedFalse(TASK_ID_1);
    }

    // getAllTasksByUserId

    @Test
    void getAllTasksByUserId_ReturnsNonDeletedTasksForUser() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        List<Task> tasksFromRepo = List.of(task1, task2);
        when(taskRepository.findByUserIdAndIsDeletedFalse(USER_ID_1)).thenReturn(tasksFromRepo);

        List<Task> result = taskService.getAllTasksByUserId(USER_ID_1);

        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
        verify(taskRepository, times(1)).findByUserIdAndIsDeletedFalse(USER_ID_1);
    }

    @Test
    void getAllTasksByUserId_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getAllTasksByUserId(NON_EXISTENT_USER_ID));
        verify(taskRepository, never()).findByUserIdAndIsDeletedFalse(anyLong());
    }

    // getPendingTasksByUserId

    @Test
    void getPendingTasksByUserId_ReturnsOnlyPendingAndNonDeletedTasks() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        List<Task> tasksFromRepo = List.of(task1, task2);
        when(taskRepository.findByUserIdAndIsDeletedFalse(USER_ID_1)).thenReturn(tasksFromRepo);

        List<Task> result = taskService.getPendingTasksByUserId(USER_ID_1);

        assertEquals(1, result.size());
        assertTrue(result.contains(task1));
        assertFalse(result.contains(task2));
        verify(taskRepository, times(1)).findByUserIdAndIsDeletedFalse(USER_ID_1);
    }

    @Test
    void getPendingTasksByUserId_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getPendingTasksByUserId(NON_EXISTENT_USER_ID));
        verify(taskRepository, never()).findByUserIdAndIsDeletedFalse(anyLong());
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
        when(taskRepository.save(any(Task.class))).thenReturn(savedTaskWithId);

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
        verify(taskRepository, times(1)).save(argThat(task ->
                task.getUserId().equals(USER_ID_1) &&
                        task.getTaskText().equals(taskToCreate.getTaskText()) &&
                        !task.getIsComplete() &&
                        !task.getIsDeleted()
        ));

        verify(notificationService, times(1))
                .addNotification(argThat(notification ->
                        notification.getUserId().equals(USER_ID_1) &&
                                notification.getTaskId().equals(savedTaskWithId.getTaskId()) &&
                                notification.getText().contains(savedTaskWithId.getTaskText()) &&
                                !notification.getIsRead()
                ));
    }

    @Test
    void createTaskForUser_ThrowsResourceNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.createTaskForUser(NON_EXISTENT_USER_ID, taskToCreate));
        verify(taskRepository, never()).save(any());
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
        verify(taskRepository, never()).save(any());
        verify(notificationService, never()).addNotification(any());
    }

    @Test
    void createTaskForUser_ThrowsIllegalArgumentException_WhenTaskTextIsBlank() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        Task taskWithBlankText = Task.builder().userId(USER_ID_1).taskText("   ").build();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTaskForUser(USER_ID_1, taskWithBlankText)
        );
        assertEquals("task text cannot be empty.", exception.getMessage());
        verify(taskRepository, never()).save(any());
        verify(notificationService, never()).addNotification(any());
    }

    @Test
    void createTaskForUser_ThrowsIllegalArgumentException_WhenTaskTextIsEmpty() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        Task taskWithEmptyText = Task.builder().userId(USER_ID_1).taskText("").build();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTaskForUser(USER_ID_1, taskWithEmptyText)
        );
        assertEquals("task text cannot be empty.", exception.getMessage());
        verify(taskRepository, never()).save(any());
        verify(notificationService, never()).addNotification(any());
    }

    // softDeleteTask

    @Test
    void softDeleteTask_MarksTaskAsDeleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByTaskIdAndIsDeletedFalse(TASK_ID_1)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        taskService.softDeleteTask(USER_ID_1, TASK_ID_1);

        verify(taskRepository, times(1)).findByTaskIdAndIsDeletedFalse(TASK_ID_1);
        verify(taskRepository, times(1)).save(argThat(task ->
                task.getTaskId().equals(TASK_ID_1) &&
                        task.getIsDeleted()
        ));
    }

    @Test
    void softDeleteTask_ThrowsResourceNotFound_WhenTaskNotFoundOrDeleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByTaskIdAndIsDeletedFalse(NON_EXISTENT_TASK_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.softDeleteTask(USER_ID_1, NON_EXISTENT_TASK_ID));
        verify(taskRepository, times(1))
                .findByTaskIdAndIsDeletedFalse(NON_EXISTENT_TASK_ID);
        verify(taskRepository, never()).save(any());
    }


    // markTaskAsCompleted

    @Test
    void markTaskAsCompleted_MarksTaskAsComplete() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByTaskIdAndIsDeletedFalse(TASK_ID_1))
                .thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.markTaskAsCompleted(USER_ID_1, TASK_ID_1);

        assertNotNull(result);
        assertTrue(result.getIsComplete());
        verify(taskRepository, times(1)).findByTaskIdAndIsDeletedFalse(TASK_ID_1);
        verify(taskRepository, times(1)).save(argThat(task ->
                task.getTaskId().equals(TASK_ID_1) &&
                        task.getIsComplete()
        ));
    }

    @Test
    void markTaskAsCompleted_ThrowsIllegalState_WhenTaskAlreadyCompleted() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByTaskIdAndIsDeletedFalse(TASK_ID_2)).thenReturn(Optional.of(task2));

        assertThrows(IllegalStateException.class,
                () -> taskService.markTaskAsCompleted(USER_ID_1, TASK_ID_2));
        verify(taskRepository, times(1)).findByTaskIdAndIsDeletedFalse(TASK_ID_2);
        verify(taskRepository, never()).save(any());
    }


    // updateTaskDetails

    @Test
    void updateTaskDetails_UpdatesTextAndDueDate() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(taskRepository.findByTaskIdAndIsDeletedFalse(TASK_ID_1))
                .thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task detailsToUpdate = Task.builder()
                .taskText("Updated Details Text")
                .dueDate(NOW.plusDays(10))
                .userId(USER_ID_1)
                .build();

        Task result = taskService.updateTaskDetails(USER_ID_1, TASK_ID_1, detailsToUpdate);

        assertNotNull(result);
        assertEquals("Updated Details Text", result.getTaskText());
        assertEquals(detailsToUpdate.getDueDate(), result.getDueDate());
        assertEquals(task1.getUserId(), result.getUserId());
        assertEquals(task1.getIsComplete(), result.getIsComplete());
        assertEquals(task1.getIsDeleted(), result.getIsDeleted());

        verify(taskRepository, times(1))
                .findByTaskIdAndIsDeletedFalse(TASK_ID_1);
        verify(taskRepository, times(1)).save(argThat(task ->
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
                .isComplete(task1.getIsComplete()).isDeleted(task1.getIsDeleted()).build();

        when(taskRepository.findByTaskIdAndIsDeletedFalse(TASK_ID_1))
                .thenReturn(Optional.of(originalTask));
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task detailsWithOnlyText = Task.builder()
                .taskText("Only Text Updated")
                .userId(USER_ID_1).build();

        Task result1 = taskService.updateTaskDetails(USER_ID_1, TASK_ID_1, detailsWithOnlyText);
        assertEquals("Only Text Updated", result1.getTaskText());
        assertEquals(originalTask.getDueDate(), result1.getDueDate());

        Task taskAfterTextUpdate = Task.builder()
                .taskId(task1.getTaskId()).userId(task1.getUserId()).taskText("Only Text Updated")
                .creationDate(task1.getCreationDate()).dueDate(task1.getDueDate())
                .isComplete(task1.getIsComplete()).isDeleted(task1.getIsDeleted()).build();
        when(taskRepository.findByTaskIdAndIsDeletedFalse(TASK_ID_1)).thenReturn(Optional.of(taskAfterTextUpdate));

        Task detailsWithOnlyDueDate = Task.builder()
                .dueDate(NOW.plusMonths(1))
                .taskText(taskAfterTextUpdate.getTaskText())
                .userId(USER_ID_1).build();

        Task result2 = taskService.updateTaskDetails(USER_ID_1, TASK_ID_1, detailsWithOnlyDueDate);
        assertEquals("Only Text Updated", result2.getTaskText());
        assertEquals(detailsWithOnlyDueDate.getDueDate(), result2.getDueDate());
    }
}


package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryTaskRepositoryImpl;
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
class InMemoryTaskRepositoryImplTest {

    private InMemoryTaskRepositoryImpl repository;

    private Task taskToSave1User1;
    private Task taskToSave2User1;
    private Task taskToSave1User2;

    private static final long USER_ID_1 = 1L;
    private static final long USER_ID_2 = 2L;
    private static final long NON_EXISTENT_TASK_ID = 999L;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepositoryImpl();

        taskToSave1User1 = Task.builder()
                .userId(USER_ID_1)
                .taskText("Task 1 User 1 Text")
                .creationDate(LocalDateTime.now().minusDays(1))
                .dueDate(LocalDateTime.now().plusDays(5))
                .isComplete(false)
                .isDeleted(false)
                .build();

        taskToSave2User1 = Task.builder()
                .userId(USER_ID_1)
                .taskText("Task 2 User 1 Text")
                .creationDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(2))
                .isComplete(true)
                .isDeleted(false)
                .build();

        taskToSave1User2 = Task.builder()
                .userId(USER_ID_2)
                .taskText("Task 1 User 2 Text")
                .creationDate(LocalDateTime.now())
                .isComplete(false)
                .isDeleted(false)
                .build();
    }

    // findAllTasks

    @Test
    void findAllTasks_ReturnsOnlyNonDeletedTasks() {
        Task savedTask1 = repository.saveTask(taskToSave1User1);
        Task savedTask2 = repository.saveTask(taskToSave1User2);

        Task taskToDelete = Task.builder().userId(USER_ID_1)
                .taskText("To Delete").isDeleted(false).build();
        Task savedTaskToDelete = repository.saveTask(taskToDelete);
        savedTaskToDelete.setIsDeleted(true);
        repository.updateTask(savedTaskToDelete);

        List<Task> receivedTasks = repository.findAllTasks();

        assertNotNull(receivedTasks);
        assertEquals(2, receivedTasks.size());
        assertTrue(receivedTasks.stream()
                .anyMatch(t -> t.getTaskId().equals(savedTask1.getTaskId())));
        assertTrue(receivedTasks.stream()
                .anyMatch(t -> t.getTaskId().equals(savedTask2.getTaskId())));
        assertFalse(receivedTasks.stream()
                .anyMatch(t -> t.getTaskId().equals(savedTaskToDelete.getTaskId())));
    }

    @Test
    void findAllTasks_ReturnsEmptyList_WhenAllTasksAreDeletedOrEmpty() {
        Task taskToDelete = Task.builder()
                .userId(USER_ID_1)
                .taskText("To Delete")
                .isDeleted(false).build();
        Task savedTaskToDelete = repository.saveTask(taskToDelete);
        savedTaskToDelete.setIsDeleted(true);
        repository.updateTask(savedTaskToDelete);

        List<Task> receivedTasks = repository.findAllTasks();
        assertNotNull(receivedTasks);
        assertTrue(receivedTasks.isEmpty());

        repository = new InMemoryTaskRepositoryImpl();
        receivedTasks = repository.findAllTasks();
        assertNotNull(receivedTasks);
        assertTrue(receivedTasks.isEmpty());
    }

    // findTaskById

    @Test
    void findTaskById_ReturnsOptionalWithTask_WhenExistsAndNotDeleted() {
        Task savedTask = repository.saveTask(taskToSave1User1);
        Optional<Task> foundTaskOpt = repository.findTaskById(savedTask.getTaskId());
        assertTrue(foundTaskOpt.isPresent());
        assertEquals(savedTask.getTaskText(), foundTaskOpt.get().getTaskText());
    }

    @Test
    void findTaskById_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.saveTask(taskToSave1User1);
        Optional<Task> foundTaskOpt = repository.findTaskById(NON_EXISTENT_TASK_ID);
        assertTrue(foundTaskOpt.isEmpty());
    }

    @Test
    void findTaskById_ReturnsEmptyOptional_WhenExistsButDeleted() {
        Task savedTask = repository.saveTask(taskToSave1User1);
        savedTask.setIsDeleted(true);
        repository.updateTask(savedTask);

        Optional<Task> foundTaskOpt = repository.findTaskById(savedTask.getTaskId());
        assertTrue(foundTaskOpt.isEmpty());
    }

    // findTasksByUserId

    @Test
    void findTasksByUserId_ReturnsOnlyNonDeletedUserTasks() {
        Task savedTask1 = repository.saveTask(taskToSave1User1);
        Task savedTask2 = repository.saveTask(taskToSave2User1);
        repository.saveTask(taskToSave1User2);

        savedTask2.setIsDeleted(true);
        repository.updateTask(savedTask2);

        List<Task> user1Tasks = repository.findTasksByUserId(USER_ID_1);
        assertNotNull(user1Tasks);
        assertEquals(1, user1Tasks.size());
        assertTrue(user1Tasks.stream()
                .anyMatch(t -> t.getTaskId().equals(savedTask1.getTaskId())));
        assertFalse(user1Tasks.stream()
                .anyMatch(t -> t.getTaskId().equals(savedTask2.getTaskId())));
    }

    @Test
    void findTasksByUserId_ReturnsEmptyList_WhenUserHasNoTasksOrAllAreDeleted() {
        repository.saveTask(taskToSave1User2);
        List<Task> user1Tasks = repository.findTasksByUserId(USER_ID_1);
        assertNotNull(user1Tasks);
        assertTrue(user1Tasks.isEmpty());

        Task savedTask1 = repository.saveTask(taskToSave1User1);
        savedTask1.setIsDeleted(true);
        repository.updateTask(savedTask1);
        user1Tasks = repository.findTasksByUserId(USER_ID_1);
        assertNotNull(user1Tasks);
        assertTrue(user1Tasks.isEmpty());
    }

    // saveTask

    @Test
    void saveTask_NewTask_AssignsIdAndReturnsSavedTask() {
        Task savedTask = repository.saveTask(taskToSave1User1);

        assertNotNull(savedTask);
        assertNotNull(savedTask.getTaskId());
        assertEquals(taskToSave1User1.getTaskText(), savedTask.getTaskText());
        assertEquals(USER_ID_1, savedTask.getUserId());

        assertTrue(repository.existsById(savedTask.getTaskId()));
        assertEquals(Optional.of(savedTask), repository.findTaskById(savedTask.getTaskId()));
    }

    @Test
    void saveTask_NewTask_DoesNotThrowDuplicate_IfCalledMultipleTimesWithDifferentObjects() {
        Task savedTask1 = repository.saveTask(taskToSave1User1);

        Task anotherTaskToSave = Task.builder()
                .userId(taskToSave1User1.getUserId())
                .taskText(taskToSave1User1.getTaskText())
                .isComplete(taskToSave1User1.getIsComplete())
                .isDeleted(taskToSave1User1.getIsDeleted())
                .build();
        Task savedTask2 = repository.saveTask(anotherTaskToSave);

        assertNotNull(savedTask1.getTaskId());
        assertNotNull(savedTask2.getTaskId());
        assertNotEquals(savedTask1.getTaskId(), savedTask2.getTaskId());
        assertEquals(2, repository.findAllTasks().size());
    }


    @Test
    void saveTask_ThrowsIllegalArgumentException_WhenTaskIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.saveTask(null)
        );
        assertEquals("task cannot be null", exception.getMessage());
    }

    // saveTask

    @Test
    void saveTask_ExistingTask_UpdatesAndReturnsTask() {
        Task savedTask = repository.saveTask(taskToSave1User1);
        Long originalId = savedTask.getTaskId();

        Task taskToUpdate = Task.builder()
                .taskId(originalId)
                .userId(savedTask.getUserId())
                .taskText("Updated Text")
                .creationDate(savedTask.getCreationDate())
                .dueDate(savedTask.getDueDate())
                .isComplete(true)
                .isDeleted(savedTask.getIsDeleted())
                .build();

        Task updatedTask = repository.saveTask(taskToUpdate);

        assertNotNull(updatedTask);
        assertEquals(originalId, updatedTask.getTaskId());
        assertEquals("Updated Text", updatedTask.getTaskText());
        assertTrue(updatedTask.getIsComplete());

        Optional<Task> foundOpt = repository.findTaskById(originalId);
        assertTrue(foundOpt.isPresent());
        assertEquals("Updated Text", foundOpt.get().getTaskText());
    }

    @Test
    void saveTask_ExistingTask_ThrowsIllegalArgumentException_WhenUpdatingNonExistingTask() {
        Task nonExistentTaskToUpdate = Task.builder()
                .taskId(NON_EXISTENT_TASK_ID)
                .userId(USER_ID_1)
                .taskText("Ghost Task")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.saveTask(nonExistentTaskToUpdate)
        );
        assertTrue(exception.getMessage()
                .contains("cannot update non-existing task with id " + NON_EXISTENT_TASK_ID));
    }


    // updateTask

    @Test
    void updateTask_UpdatesExistingTask_IncludingIsDeleted() {
        Task savedTask = repository.saveTask(taskToSave1User1);
        Long taskId = savedTask.getTaskId();

        savedTask.setIsDeleted(true);
        savedTask.setTaskText("Soft Deleted Task");
        Task updatedTask = repository.updateTask(savedTask);

        assertNotNull(updatedTask);
        assertEquals(taskId, updatedTask.getTaskId());
        assertTrue(updatedTask.getIsDeleted());
        assertEquals("Soft Deleted Task", updatedTask.getTaskText());

        Optional<Task> foundOpt = repository.findTaskById(taskId);
        assertTrue(foundOpt.isEmpty());

        List<Task> allTasks = repository.findAllTasks();
        assertFalse(allTasks.stream().anyMatch(t -> t.getTaskId().equals(taskId)));
    }

    @Test
    void updateTask_ThrowsIllegalArgumentException_WhenTaskIsNull() {
        assertThrows(IllegalArgumentException.class, () -> repository.updateTask(null));
    }

    @Test
    void updateTask_ThrowsIllegalArgumentException_WhenTaskIdIsNull() {
        Task taskWithNullId = Task.builder()
                .userId(USER_ID_1)
                .taskText("text").build();
        assertThrows(IllegalArgumentException.class, () -> repository.updateTask(taskWithNullId));
    }

    @Test
    void updateTask_ThrowsIllegalArgumentException_WhenTaskNotFoundForUpdate() {
        Task nonExistentTask = Task.builder()
                .taskId(NON_EXISTENT_TASK_ID)
                .userId(USER_ID_1)
                .taskText("text").build();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.updateTask(nonExistentTask)
        );
        assertTrue(exception.getMessage()
                .contains("task with id " + NON_EXISTENT_TASK_ID + " not found for update."));
    }

    // existsById

    @Test
    void existsById_ReturnsTrue_WhenExistsAndNotDeleted() {
        Task savedTask = repository.saveTask(taskToSave1User1);
        assertTrue(repository.existsById(savedTask.getTaskId()));
    }

    @Test
    void existsById_ReturnsFalse_WhenDoesNotExist() {
        assertFalse(repository.existsById(NON_EXISTENT_TASK_ID));
    }

    @Test
    void existsById_ReturnsFalse_WhenExistsButDeleted() {
        Task savedTask = repository.saveTask(taskToSave1User1);
        savedTask.setIsDeleted(true);
        repository.updateTask(savedTask);
        assertFalse(repository.existsById(savedTask.getTaskId()));
    }

}
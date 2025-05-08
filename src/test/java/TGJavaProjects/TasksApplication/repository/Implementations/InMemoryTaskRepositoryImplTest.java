package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("in-memory")
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
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();

        taskToSave2User1 = Task.builder()
                .userId(USER_ID_1)
                .taskText("Task 2 User 1 Text")
                .creationDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(2))
                .isComplete(true)
                .build();

        taskToSave1User2 = Task.builder()
                .userId(USER_ID_2)
                .taskText("Task 1 User 2 Text")
                .build();
    }

    // findAllNonDeleted

    @Test
    void findAllNonDeleted_ReturnsOnlyNonDeletedTasks() {
        Task savedTask1 = repository.save(taskToSave1User1);
        Task savedTask2 = repository.save(taskToSave1User2);

        Task taskToDelete = Task.builder()
                .userId(USER_ID_1)
                .taskText("To Delete").build();
        Task savedTaskToDelete = repository.save(taskToDelete);
        savedTaskToDelete.setIsDeleted(true);
        repository.save(savedTaskToDelete);

        List<Task> receivedTasks = repository.findByIsDeletedFalse();

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
    void findAllNonDeleted_ReturnsEmptyList_WhenAllTasksAreDeletedOrEmpty() {
        Task taskToDelete = Task.builder()
                .userId(USER_ID_1)
                .taskText("To Delete").build();
        Task savedTaskToDelete = repository.save(taskToDelete);
        savedTaskToDelete.setIsDeleted(true);
        repository.save(savedTaskToDelete);

        List<Task> receivedTasks = repository.findByIsDeletedFalse();
        assertNotNull(receivedTasks);
        assertTrue(receivedTasks.isEmpty());

        repository = new InMemoryTaskRepositoryImpl();
        receivedTasks = repository.findByIsDeletedFalse();
        assertNotNull(receivedTasks);
        assertTrue(receivedTasks.isEmpty());
    }

    // findByIdNonDeleted

    @Test
    void findByTaskIdAndIsDeleted_ReturnsOptionalWithTask_WhenExistsAndNotDeletedFalse() {
        Task savedTask = repository.save(taskToSave1User1);
        Optional<Task> foundTaskOpt = repository.findByTaskIdAndIsDeletedFalse(savedTask.getTaskId());
        assertTrue(foundTaskOpt.isPresent());
        assertEquals(savedTask.getTaskText(), foundTaskOpt.get().getTaskText());
    }

    @Test
    void findByTaskIdAndIsDeleted_False_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.save(taskToSave1User1);
        Optional<Task> foundTaskOpt = repository.findByTaskIdAndIsDeletedFalse(NON_EXISTENT_TASK_ID);
        assertTrue(foundTaskOpt.isEmpty());
    }

    @Test
    void findByTaskIdAndIsDeleted_ReturnsEmptyOptional_WhenExistsButDeletedFalse() {
        Task savedTask = repository.save(taskToSave1User1);
        savedTask.setIsDeleted(true);
        repository.save(savedTask);

        Optional<Task> foundTaskOpt = repository.findByTaskIdAndIsDeletedFalse(savedTask.getTaskId());
        assertTrue(foundTaskOpt.isEmpty());
    }

    @Test
    void findByIdNonDeleted_ReturnsEmptyOptional_WhenTaskIdIsNullFalse() {
        repository.save(taskToSave1User1);
        Optional<Task> foundTaskOpt = repository.findByTaskIdAndIsDeletedFalse(null);
        assertTrue(foundTaskOpt.isEmpty());
    }


    // findTasksByUserId

    @Test
    void findTasksByUserId_ReturnsOnlyNonDeletedUserTasks() {
        Task savedTask1 = repository.save(taskToSave1User1);
        Task savedTask2 = repository.save(taskToSave2User1);
        repository.save(taskToSave1User2);

        savedTask2.setIsDeleted(true);
        repository.save(savedTask2);

        List<Task> user1Tasks = repository.findByUserIdAndIsDeletedFalse(USER_ID_1);
        assertNotNull(user1Tasks);
        assertEquals(1, user1Tasks.size());
        assertTrue(user1Tasks.stream()
                .anyMatch(t -> t.getTaskId().equals(savedTask1.getTaskId())));
        assertFalse(user1Tasks.stream()
                .anyMatch(t -> t.getTaskId().equals(savedTask2.getTaskId())));
    }

    @Test
    void findTasksByUserId_ReturnsEmptyList_WhenUserHasNoTasksOrAllAreDeleted() {
        repository.save(taskToSave1User2);
        List<Task> user1Tasks = repository.findByUserIdAndIsDeletedFalse(USER_ID_1);
        assertNotNull(user1Tasks);
        assertTrue(user1Tasks.isEmpty());

        Task savedTask1 = repository.save(taskToSave1User1);
        savedTask1.setIsDeleted(true);
        repository.save(savedTask1);
        user1Tasks = repository.findByUserIdAndIsDeletedFalse(USER_ID_1);
        assertNotNull(user1Tasks);
        assertTrue(user1Tasks.isEmpty());
    }

    @Test
    void findTasksByUserId_ReturnsEmptyList_WhenUserIdIsNull() {
        repository.save(taskToSave1User1);
        List<Task> userTasks = repository.findByUserIdAndIsDeletedFalse(null);
        assertNotNull(userTasks);
        assertTrue(userTasks.isEmpty());
    }


    // save

    @Test
    void save_NewTask_AssignsIdAndReturnsSavedTask() {
        Task savedTask = repository.save(taskToSave1User1);

        assertNotNull(savedTask);
        assertNotNull(savedTask.getTaskId());
        assertEquals(taskToSave1User1.getTaskText(), savedTask.getTaskText());
        assertEquals(USER_ID_1, savedTask.getUserId());
        assertNotNull(savedTask.getCreationDate());
        assertNotNull(savedTask.getIsComplete());
        assertNotNull(savedTask.getIsDeleted());


        assertTrue(repository.existsByTaskIdAndIsDeletedFalse(savedTask.getTaskId()));
        assertEquals(Optional.of(savedTask), repository
                .findByTaskIdAndIsDeletedFalse(savedTask.getTaskId()));
    }

    @Test
    void save_NewTask_DoesNotThrowDuplicate_IfCalledMultipleTimesWithDifferentObjects() {
        Task savedTask1 = repository.save(taskToSave1User1);

        Task anotherTaskToSave = Task.builder()
                .userId(taskToSave1User1.getUserId())
                .taskText(taskToSave1User1.getTaskText())
                .build();
        Task savedTask2 = repository.save(anotherTaskToSave);

        assertNotNull(savedTask1.getTaskId());
        assertNotNull(savedTask2.getTaskId());
        assertNotEquals(savedTask1.getTaskId(), savedTask2.getTaskId());
        assertEquals(2, repository.findByIsDeletedFalse().size());
    }


    @Test
    void save_ThrowsIllegalArgumentException_WhenTaskIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(null)
        );
        assertEquals("task cannot be null", exception.getMessage());
    }

    @Test
    void save_ExistingTask_UpdatesAndReturnsTask() {
        Task savedTask = repository.save(taskToSave1User1);
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

        Task updatedTask = repository.save(taskToUpdate);

        assertNotNull(updatedTask);
        assertEquals(originalId, updatedTask.getTaskId());
        assertEquals("Updated Text", updatedTask.getTaskText());
        assertTrue(updatedTask.getIsComplete());

        Optional<Task> foundOpt = repository.findByTaskIdAndIsDeletedFalse(originalId);
        assertTrue(foundOpt.isPresent());
        assertEquals("Updated Text", foundOpt.get().getTaskText());
    }

    @Test
    void save_ExistingTask_CanMarkAsDeleted() {
        Task savedTask = repository.save(taskToSave1User1);
        Long originalId = savedTask.getTaskId();

        Task taskToUpdate = Task.builder()
                .taskId(originalId)
                .userId(savedTask.getUserId())
                .taskText(savedTask.getTaskText())
                .creationDate(savedTask.getCreationDate())
                .dueDate(savedTask.getDueDate())
                .isComplete(savedTask.getIsComplete())
                .isDeleted(true)
                .build();

        Task updatedTask = repository.save(taskToUpdate);

        assertNotNull(updatedTask);
        assertEquals(originalId, updatedTask.getTaskId());
        assertTrue(updatedTask.getIsDeleted());

        assertFalse(repository.findByTaskIdAndIsDeletedFalse(originalId).isPresent());
        assertFalse(repository.existsByTaskIdAndIsDeletedFalse(originalId));
        assertTrue(repository.findByUserIdAndIsDeletedFalse(USER_ID_1).isEmpty());
        assertTrue(repository.findByIsDeletedFalse().isEmpty());

        assertTrue(repository.findById(originalId).isPresent());
        assertTrue(repository.existsById(originalId));
    }


    @Test
    void save_ExistingTask_ThrowsIllegalArgumentException_WhenUpdatingNonExistingTask() {
        Task nonExistentTaskToUpdate = Task.builder()
                .taskId(NON_EXISTENT_TASK_ID)
                .userId(USER_ID_1)
                .taskText("Ghost Task")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(nonExistentTaskToUpdate)
        );
        assertTrue(exception.getMessage()
                .contains("Task with id " + NON_EXISTENT_TASK_ID + " not found for update via save."));
    }

    // existsByIdNonDeleted

    @Test
    void existsByIdNonDeleted_ReturnsTrue_WhenExistsAndNotDeletedTask() {
        Task savedTask = repository.save(taskToSave1User1);
        assertTrue(repository.existsByTaskIdAndIsDeletedFalse(savedTask.getTaskId()));
    }

    @Test
    void existsByTaskIdAndIsDeleted_False_ReturnsFalse_WhenDoesNotExist() {
        assertFalse(repository.existsByTaskIdAndIsDeletedFalse(NON_EXISTENT_TASK_ID));
    }

    @Test
    void existsByIdNonDeleted_ReturnsFalse_WhenExistsButDeletedTask() {
        Task savedTask = repository.save(taskToSave1User1);
        savedTask.setIsDeleted(true);
        repository.save(savedTask);
        assertFalse(repository.existsByTaskIdAndIsDeletedFalse(savedTask.getTaskId()));
    }

    @Test
    void existsByIdNonDeleted_ReturnsFalse_WhenTaskIdIsNullFalse() {
        repository.save(taskToSave1User1);
        assertFalse(repository.existsByTaskIdAndIsDeletedFalse(null));
    }

}
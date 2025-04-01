package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.InMemoryTaskDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryTaskServiceImplTest {
    private static final long TASK_ID = 1L;
    private static final Task task1 = mock(Task.class);
    private static final Task task2 = mock(Task.class);

    @Mock
    private InMemoryTaskDAO repository;

    @InjectMocks
    private InMemoryTaskServiceImpl service;

//    @BeforeEach
//    void setUp() {
//       // service = new InMemoryTaskServiceImpl();
//    }

    @Test
    void getAllTasks() {
    }

    @Test
    void InMemoryTaskServiceImplTest_GetTaskById_ReturnOptional() {
        //when(task1.getTaskId()).thenReturn(TASK_ID);
        when(repository.getTaskById(TASK_ID)).thenReturn(task1);

        Optional<Task> receivedTask = service.getTaskById(TASK_ID);

        assertEquals(Optional.of(task1), receivedTask);
        verify(repository).getTaskById(TASK_ID);
    }

    @Test
    void getTasksByUserId() {
    }

    @Test
    void addTask() {
    }

    @Test
    void deleteTask() {
    }
}
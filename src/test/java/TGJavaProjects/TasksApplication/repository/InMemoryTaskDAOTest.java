package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;

@SpringBootTest
class InMemoryTaskDAOTest {

    @Autowired
    private InMemoryTaskDAO repository;

    @Test
    void getAllTasks() {
    }

    @Test
    void InMemoryTaskDAO_GetTaskById_ReturnTask() {
        // arrange


        // act


        // assert

    }

    @Test
    void getTasksByUserId() {
    }

    @Test
    void InMemoryTaskDAO_AddTask_ReturnSavedTask() {
        // arrange
        final Task task = Task.builder().taskText("").creationDate(LocalDateTime.now()).build();

        // act
        Task savedTask = repository.addTask(task);

        // assert
        Assertions.assertNotNull(savedTask);
        Assertions.assertEquals(task, savedTask);
    }

    @Test
    void deleteTask() {
    }
}
package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Мок в Spring-контексте
    private TaskService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "test task",
                LocalDateTime.now(), LocalDateTime.now(), true, 1L);
    }

    @Test
    void getAllTasks_ReturnsListOfTasks() throws Exception {
        when(service.getAllTasks()).thenReturn(Collections.singletonList(task));

        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(service, times(1)).getAllTasks();
    }

    @Test
    void getTaskById_ReturnsTask_WhenTaskExists() throws Exception {
        String taskJson = objectMapper.writeValueAsString(task);

        when(service.getTaskById(task.getTaskId())).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/v1/tasks/" + task.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskText").value(task.getTaskText()))
                .andExpect(jsonPath("$.dueDate")
                        .value(Matchers.containsString(task.getDueDate().toString().substring(0, 23))))
                .andExpect(jsonPath("$.creationDate")
                        .value(Matchers.containsString(task.getCreationDate().toString().substring(0, 23))))
                .andExpect(jsonPath("$.complete").value((boolean) task.isComplete()))
                .andExpect(jsonPath("$.userId").value(task.getUserId()));

        verify(service, times(1)).getTaskById(task.getTaskId());
    }

    @Test
    void getTaskById_ReturnsIsNotFound_WhenTaskDoesNotExist() throws Exception {
        when(service.getTaskById(task.getTaskId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tasks/" + task.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getTaskById(task.getTaskId());
    }

    @Test
    void getTasksByUserId_ReturnsListOfTasks_WhenUserExists() throws Exception {
        String taskJson = objectMapper.writeValueAsString(Collections.singletonList(task));

        when(service.getTasksByUserId(task.getUserId()))
                .thenReturn(Optional.of(Collections.singletonList(task)));

        mockMvc.perform(get("/api/v1/tasks/user/" + task.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(service, times(1)).getTasksByUserId(task.getUserId());
    }

    @Test
    void getTasksByUserId_ReturnsIsNotFound_WhenUserDoesNotExist() throws Exception {
        when(service.getTasksByUserId(task.getUserId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tasks/user/" + task.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getTasksByUserId(task.getUserId());
    }

    @Test
    void addTask_ReturnsTask_WhenTaskIsValid() throws Exception {
        String taskJson = objectMapper.writeValueAsString(task);

        when(service.addTask(any(Task.class))).thenReturn(Optional.of(task));

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskText").value(task.getTaskText()))
                .andExpect(jsonPath("$.dueDate")
                        .value(Matchers.containsString(task.getDueDate().toString().substring(0, 23))))
                .andExpect(jsonPath("$.creationDate")
                        .value(Matchers.containsString(task.getCreationDate().toString().substring(0, 23))))
                .andExpect(jsonPath("$.complete").value((boolean) task.isComplete()))
                .andExpect(jsonPath("$.userId").value(task.getUserId()));

        verify(service, times(1)).addTask(any(Task.class));
    }

    @Test
    void addTask_ReturnsIsBadRequest_WhenTaskIsNotValid() throws Exception {
        when(service.addTask(any(Task.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(0)).addTask(any(Task.class));
    }

    @Test
    void deleteTask_ReturnsTask_WhenTaskExists() throws Exception {
        String taskJson = objectMapper.writeValueAsString(task);

        when(service.deleteTask(task.getTaskId())).thenReturn(Optional.of(task));

        mockMvc.perform(delete("/api/v1/tasks/" + task.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskText").value(task.getTaskText()))
                .andExpect(jsonPath("$.dueDate")
                        .value(Matchers.containsString(task.getDueDate().toString().substring(0, 23))))
                .andExpect(jsonPath("$.creationDate")
                        .value(Matchers.containsString(task.getCreationDate().toString().substring(0, 23))))
                .andExpect(jsonPath("$.complete").value((boolean) task.isComplete()))
                .andExpect(jsonPath("$.userId").value(task.getUserId()));

        verify(service, times(1)).deleteTask(task.getTaskId());
    }

    @Test
    void deleteTask_ReturnsIsBadRequest_WhenTaskDoesNotExists() throws Exception {
        when(service.deleteTask(task.getTaskId())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/v1/tasks/" + task.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).deleteTask(task.getTaskId());
    }

}
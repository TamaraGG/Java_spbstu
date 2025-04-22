package TGJavaProjects.TasksApplication.controller;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task1;
    private Task task2;
    private static final long USER_ID_1 = 1L;
    private static final long TASK_ID_1 = 10L;
    private static final long TASK_ID_2 = 20L;
    private static final long NON_EXISTENT_TASK_ID = 4L;
    private static final long NON_EXISTENT_USER_ID = 3L;


    private Task task;

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


    // getAllTasks

    @Test
    void getAllTasks_ReturnsListOfTasks() throws Exception {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.findAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].taskId", is((int) TASK_ID_1)))
                .andExpect(jsonPath("$[1].taskId", is((int) TASK_ID_2)));

        verify(taskService, times(1)).findAllTasks();
    }

    @Test
    void getAllTasks_ReturnsEmptyList() throws Exception {
        when(taskService.findAllTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).findAllTasks();
    }

    // getTaskById

    @Test
    void getTaskById_ReturnsTask_WhenExists() throws Exception {
        when(taskService.findTaskById(TASK_ID_1)).thenReturn(task1);

        mockMvc.perform(get("/api/v1/tasks/{id}", TASK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId", is(task1.getTaskId().intValue())))
                .andExpect(jsonPath("$.taskText", is(task1.getTaskText())))
                .andExpect(jsonPath("$.userId", is(task1.getUserId().intValue())))
                .andExpect(jsonPath("$.isComplete", is(task1.getIsComplete())));

        verify(taskService, times(1)).findTaskById(TASK_ID_1);
    }

    @Test
    void getTaskById_ReturnsNotFound_WhenDoesNotExist() throws Exception {
        when(taskService.findTaskById(NON_EXISTENT_TASK_ID))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(get("/api/v1/tasks/{id}", NON_EXISTENT_TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).findTaskById(NON_EXISTENT_TASK_ID);
    }

    // getTasksByUserId

    @Test
    void getTasksByUserId_ReturnsListOfTasks_WhenUserExists() throws Exception {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.findTasksByUserId(USER_ID_1)).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks/user/{userId}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].taskId", is((int) TASK_ID_1)))
                .andExpect(jsonPath("$[1].taskId", is((int) TASK_ID_2)));

        verify(taskService, times(1)).findTasksByUserId(USER_ID_1);
    }

    @Test
    void getTasksByUserId_ReturnsEmptyList_WhenUserExistsButNoTasks() throws Exception {
        when(taskService.findTasksByUserId(USER_ID_1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/tasks/user/{userId}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).findTasksByUserId(USER_ID_1);
    }


    @Test
    void getTasksByUserId_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(taskService.findTasksByUserId(NON_EXISTENT_USER_ID))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/tasks/user/{userId}", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).findTasksByUserId(NON_EXISTENT_USER_ID);
    }

    // addTask

    @Test
    void addTask_ReturnsTask_WhenValid() throws Exception {
        when(taskService.addTask(any(Task.class))).thenReturn(task1);
        String taskJson = objectMapper.writeValueAsString(task1);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/tasks/" + task1.getTaskId()))
                .andExpect(jsonPath("$.taskId", is(task1.getTaskId().intValue())))
                .andExpect(jsonPath("$.taskText", is(task1.getTaskText())));

        verify(taskService, times(1)).addTask(any(Task.class));
    }

    @Test
    void addTask_ReturnsBadRequest_WhenUserNotFound() throws Exception {
        when(taskService.addTask(any(Task.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));
        String taskJson = objectMapper.writeValueAsString(task1);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isBadRequest());

        verify(taskService, times(1)).addTask(any(Task.class));
    }

    @Test
    void addTask_ReturnsConflict_WhenTaskIdExists() throws Exception {
        when(taskService.addTask(any(Task.class)))
                .thenThrow(new DuplicateResourceException("Task ID exists"));
        String taskJson = objectMapper.writeValueAsString(task1);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isConflict());

        verify(taskService, times(1)).addTask(any(Task.class));
    }

    @Test
    void addTask_ReturnsBadRequest_WhenIllegalArgument() throws Exception {
        when(taskService.addTask(any(Task.class)))
                .thenThrow(new IllegalArgumentException("Invalid input"));
        String taskJson = objectMapper.writeValueAsString(task1);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isBadRequest());

        verify(taskService, times(1)).addTask(any(Task.class));
    }

    @Test
    void addTask_ReturnsBadRequest_WhenInvalidJson() throws Exception {
        String invalidJson = "{\"taskId\": 1, \"taskText\": \"Test\",";

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).addTask(any(Task.class));
    }


    // deleteTask

    @Test
    void deleteTask_ReturnsNoContent_WhenSuccessful() throws Exception {
        doNothing().when(taskService).deleteTask(TASK_ID_1);

        mockMvc.perform(delete("/api/v1/tasks/{id}", TASK_ID_1))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(TASK_ID_1);
    }

    @Test
    void deleteTask_ReturnsNotFound_WhenTaskDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found")).when(taskService).deleteTask(NON_EXISTENT_TASK_ID);

        mockMvc.perform(delete("/api/v1/tasks/{id}", NON_EXISTENT_TASK_ID))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteTask(NON_EXISTENT_TASK_ID);
    }

}
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TaskController.class)
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task1, task2, task1Completed, task1Deleted;
    private TaskRequest taskRequest1;

    private static final long USER_ID_1 = 1L;
    private static final long TASK_ID_1 = 10L;
    private static final long TASK_ID_2 = 20L;
    private static final long NON_EXISTENT_USER_ID = 99L;
    private static final long NON_EXISTENT_TASK_ID = 999L;
    private static final String TASK_TEXT_1 = "Task 1 Text";
    private static final String TASK_TEXT_2 = "Task 2 Text";
    private static final LocalDateTime NOW = LocalDateTime.now();


    @BeforeEach
    void setUp() {
        taskRequest1 = new TaskRequest(TASK_TEXT_1, NOW.plusDays(5));

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
                .taskText(TASK_TEXT_2)
                .creationDate(NOW)
                .isComplete(false)
                .isDeleted(false)
                .build();

        task1Completed = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText(TASK_TEXT_1)
                .creationDate(NOW.minusDays(1))
                .dueDate(NOW.plusDays(5))
                .isComplete(true)
                .isDeleted(false)
                .build();

        task1Deleted = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText(TASK_TEXT_1)
                .creationDate(NOW.minusDays(1))
                .dueDate(NOW.plusDays(5))
                .isComplete(false)
                .isDeleted(true)
                .build();
    }

    // getAllUserTasks

    @Test
    void getAllUserTasks_ReturnsListOfTasks_WhenUserExists() throws Exception {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasksByUserId(USER_ID_1)).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/users/{userId}/tasks", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].taskId", is((int) TASK_ID_1)))
                .andExpect(jsonPath("$[1].taskId", is((int) TASK_ID_2)));

        verify(taskService, times(1)).getAllTasksByUserId(USER_ID_1);
    }

    @Test
    void getAllUserTasks_ReturnsEmptyList_WhenUserHasNoTasks() throws Exception {
        when(taskService.getAllTasksByUserId(USER_ID_1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/{userId}/tasks", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(taskService, times(1)).getAllTasksByUserId(USER_ID_1);
    }

    @Test
    void getAllUserTasks_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(taskService.getAllTasksByUserId(NON_EXISTENT_USER_ID))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{userId}/tasks", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).getAllTasksByUserId(NON_EXISTENT_USER_ID);
    }

    // getPendingUserTasks

    @Test
    void getPendingUserTasks_ReturnsListOfPendingTasks() throws Exception {
        List<Task> pendingTasks = Arrays.asList(task1, task2);
        when(taskService.getPendingTasksByUserId(USER_ID_1)).thenReturn(pendingTasks);

        mockMvc.perform(get("/api/v1/users/{userId}/tasks/pending", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].taskId", is((int) TASK_ID_1)))
                .andExpect(jsonPath("$[0].isComplete", is(false)))
                .andExpect(jsonPath("$[1].taskId", is((int) TASK_ID_2)))
                .andExpect(jsonPath("$[1].isComplete", is(false)));

        verify(taskService, times(1)).getPendingTasksByUserId(USER_ID_1);
    }

    @Test
    void getPendingUserTasks_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(taskService.getPendingTasksByUserId(NON_EXISTENT_USER_ID))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{userId}/tasks/pending", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).getPendingTasksByUserId(NON_EXISTENT_USER_ID);
    }

    // getTaskByIdForUser

    @Test
    void getTaskByIdForUser_ReturnsTask_WhenExists() throws Exception {
        when(taskService.findTaskById(USER_ID_1, TASK_ID_1)).thenReturn(task1);

        mockMvc.perform(get("/api/v1/users/{userId}/tasks/{taskId}", USER_ID_1, TASK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId", is((int) TASK_ID_1)))
                .andExpect(jsonPath("$.taskText", is(task1.getTaskText())));
        verify(taskService, times(1)).findTaskById(USER_ID_1, TASK_ID_1);
    }

    @Test
    void getTaskByIdForUser_ReturnsNotFound_WhenTaskDoesNotExist() throws Exception {
        when(taskService.findTaskById(USER_ID_1, NON_EXISTENT_TASK_ID))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(get("/api/v1/users/{userId}/tasks/{taskId}", USER_ID_1, NON_EXISTENT_TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).findTaskById(USER_ID_1, NON_EXISTENT_TASK_ID);
    }

    @Test
    void getTaskByIdForUser_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(taskService.findTaskById(NON_EXISTENT_USER_ID, TASK_ID_1))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{userId}/tasks/{taskId}", NON_EXISTENT_USER_ID, TASK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).findTaskById(NON_EXISTENT_USER_ID, TASK_ID_1);
    }

    // createTaskForUser

    @Test
    void createTaskForUser_ReturnsCreatedTask_WhenValid() throws Exception {
        Task createdTask = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText(taskRequest1.getTaskText())
                .dueDate(taskRequest1.getDueDate())
                .creationDate(LocalDateTime.now())
                .isComplete(false)
                .isDeleted(false)
                .build();

        when(taskService.createTaskForUser(eq(USER_ID_1), any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/v1/users/{userId}/tasks", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest1)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        String.format("/api/v1/users/%d/tasks/%d", USER_ID_1, createdTask.getTaskId())))
                .andExpect(jsonPath("$.taskId", is(createdTask.getTaskId().intValue())))
                .andExpect(jsonPath("$.taskText", is(createdTask.getTaskText())));

        verify(taskService, times(1)).createTaskForUser(eq(USER_ID_1), argThat(task ->
                task.getTaskText().equals(taskRequest1.getTaskText()) &&
                        task.getDueDate().equals(taskRequest1.getDueDate())
        ));
    }

    @Test
    void createTaskForUser_ReturnsBadRequest_WhenTaskTextIsNull() throws Exception {
        TaskRequest invalidRequest = new TaskRequest(null, LocalDateTime.now());
        mockMvc.perform(post("/api/v1/users/{userId}/tasks", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("task text cannot be empty.",
                        ((ResponseStatusException) result.getResolvedException()).getReason()));
        verify(taskService, never()).createTaskForUser(anyLong(), any(Task.class));
    }

    @Test
    void createTaskForUser_ReturnsBadRequest_WhenTaskTextIsBlank() throws Exception {
        TaskRequest invalidRequest = new TaskRequest(" ", LocalDateTime.now());
        mockMvc.perform(post("/api/v1/users/{userId}/tasks", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("task text cannot be empty.",
                        ((ResponseStatusException) result.getResolvedException()).getReason()));
        verify(taskService, never()).createTaskForUser(anyLong(), any(Task.class));
    }

    @Test
    void createTaskForUser_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(taskService.createTaskForUser(eq(NON_EXISTENT_USER_ID), any(Task.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(post("/api/v1/users/{userId}/tasks", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest1)))
                .andExpect(status().isNotFound());
        verify(taskService, times(1))
                .createTaskForUser(eq(NON_EXISTENT_USER_ID), any(Task.class));
    }

    @Test
    void createTaskForUser_ReturnsConflict_WhenServiceThrowsDuplicate() throws Exception {
        when(taskService.createTaskForUser(eq(USER_ID_1), any(Task.class)))
                .thenThrow(new DuplicateResourceException("Task already exists"));

        mockMvc.perform(post("/api/v1/users/{userId}/tasks", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest1)))
                .andExpect(status().isConflict());
        verify(taskService, times(1)).createTaskForUser(eq(USER_ID_1), any(Task.class));
    }


    // softDeleteTask

    @Test
    void softDeleteTask_ReturnsNoContent_WhenSuccessful() throws Exception {
        doNothing().when(taskService).softDeleteTask(USER_ID_1, TASK_ID_1);

        mockMvc.perform(delete("/api/v1/users/{userId}/tasks/{taskId}", USER_ID_1, TASK_ID_1))
                .andExpect(status().isNoContent());
        verify(taskService, times(1)).softDeleteTask(USER_ID_1, TASK_ID_1);
    }

    @Test
    void softDeleteTask_ReturnsNotFound_WhenTaskDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found"))
                .when(taskService).softDeleteTask(USER_ID_1, NON_EXISTENT_TASK_ID);

        mockMvc.perform(delete("/api/v1/users/{userId}/tasks/{taskId}", USER_ID_1, NON_EXISTENT_TASK_ID))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).softDeleteTask(USER_ID_1, NON_EXISTENT_TASK_ID);
    }

    @Test
    void softDeleteTask_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(taskService).softDeleteTask(NON_EXISTENT_USER_ID, TASK_ID_1);

        mockMvc.perform(delete("/api/v1/users/{userId}/tasks/{taskId}", NON_EXISTENT_USER_ID, TASK_ID_1))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).softDeleteTask(NON_EXISTENT_USER_ID, TASK_ID_1);
    }


    // markTaskAsCompleted

    @Test
    void markTaskAsCompleted_ReturnsOkWithTask_WhenSuccessful() throws Exception {
        when(taskService.markTaskAsCompleted(USER_ID_1, TASK_ID_1)).thenReturn(task1Completed);

        mockMvc.perform(put("/api/v1/users/{userId}/tasks/{taskId}/complete", USER_ID_1, TASK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId", is((int) TASK_ID_1)))
                .andExpect(jsonPath("$.isComplete", is(true)));
        verify(taskService, times(1)).markTaskAsCompleted(USER_ID_1, TASK_ID_1);
    }

    @Test
    void markTaskAsCompleted_ReturnsNotFound_WhenTaskDoesNotExist() throws Exception {
        when(taskService.markTaskAsCompleted(USER_ID_1, NON_EXISTENT_TASK_ID))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(put("/api/v1/users/{userId}/tasks/{taskId}/complete", USER_ID_1, NON_EXISTENT_TASK_ID))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).markTaskAsCompleted(USER_ID_1, NON_EXISTENT_TASK_ID);
    }

    @Test
    void markTaskAsCompleted_ReturnsConflict_WhenTaskAlreadyCompleted() throws Exception {
        when(taskService.markTaskAsCompleted(USER_ID_1, TASK_ID_1))
                .thenThrow(new IllegalStateException("Task is already completed"));

        mockMvc.perform(put("/api/v1/users/{userId}/tasks/{taskId}/complete", USER_ID_1, TASK_ID_1))
                .andExpect(status().isConflict());
        verify(taskService, times(1)).markTaskAsCompleted(USER_ID_1, TASK_ID_1);
    }

    // updateTaskDetails

    @Test
    void updateTaskDetails_ReturnsOkWithUpdatedTask_WhenValid() throws Exception {
        TaskRequest updateRequest = new TaskRequest("Updated Task Text", NOW.plusDays(10));
        Task updatedTask = Task.builder()
                .taskId(TASK_ID_1)
                .userId(USER_ID_1)
                .taskText(updateRequest.getTaskText())
                .dueDate(updateRequest.getDueDate())
                .creationDate(task1.getCreationDate())
                .isComplete(task1.getIsComplete())
                .isDeleted(task1.getIsDeleted())
                .build();
        when(taskService.updateTaskDetails(eq(USER_ID_1), eq(TASK_ID_1), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/users/{userId}/tasks/{taskId}", USER_ID_1, TASK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskText", is(updateRequest.getTaskText())));

        verify(taskService, times(1))
                .updateTaskDetails(eq(USER_ID_1), eq(TASK_ID_1), argThat(task ->
                        task.getTaskText().equals(updateRequest.getTaskText()) &&
                                task.getDueDate().equals(updateRequest.getDueDate())
        ));
    }

    @Test
    void updateTaskDetails_ReturnsNotFound_WhenTaskDoesNotExist() throws Exception {
        TaskRequest updateRequest = new TaskRequest("Updated Text", NOW);
        when(taskService.updateTaskDetails(eq(USER_ID_1), eq(NON_EXISTENT_TASK_ID), any(Task.class)))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(put("/api/v1/users/{userId}/tasks/{taskId}", USER_ID_1, NON_EXISTENT_TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
        verify(taskService, times(1))
                .updateTaskDetails(eq(USER_ID_1), eq(NON_EXISTENT_TASK_ID), any(Task.class));
    }

    @Test
    void updateTaskDetails_ReturnsBadRequest_WhenIllegalArgumentInService() throws Exception {
        TaskRequest updateRequest = new TaskRequest("", NOW); // Пустой текст, например
        when(taskService.updateTaskDetails(eq(USER_ID_1), eq(TASK_ID_1), any(Task.class)))
                .thenThrow(new IllegalArgumentException("Task text cannot be blank"));

        mockMvc.perform(put("/api/v1/users/{userId}/tasks/{taskId}", USER_ID_1, TASK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
        verify(taskService, times(1))
                .updateTaskDetails(eq(USER_ID_1), eq(TASK_ID_1), any(Task.class));
    }

}
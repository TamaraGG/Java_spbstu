package TGJavaProjects.TasksApplication.controller;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Мок в Spring-контексте
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Doe", "Jane", "email@com");
    }
    @Test
    void getAllUsers_ReturnsListOfUsers() throws Exception {
        when(service.findAllUsers()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
        verify(service, times(1)).findAllUsers();
    }

    @Test
    void getUserById_ReturnsUser_WhenExists() throws Exception {
        String userJson = objectMapper.writeValueAsString(user);
        when(service.findUserById(user.getUserId())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/" + user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value((int) user.getUserId()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        verify(service, times(1)).findUserById(user.getUserId());
    }

    @Test
    void getUserById_ReturnsNotFound_WhenDoesNotExist() throws Exception {
        when(service.findUserById(user.getUserId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/" + user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).findUserById(user.getUserId());
    }

    @Test
    void addUser_ReturnsUser_WhenValidUser() throws Exception {
        String userJson = objectMapper.writeValueAsString(user);
        when(service.addUser(any(User.class))).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value((int) user.getUserId()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        verify(service, times(1)).addUser(any(User.class));
    }

    @Test
    void addUser_ReturnsBadRequest_WhenNotValidUser() throws Exception {

        when(service.addUser(any(User.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(0)).addUser(any(User.class));
    }
}
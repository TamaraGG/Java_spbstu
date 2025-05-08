package TGJavaProjects.TasksApplication.controller;



import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private UserRegistrationRequest userRegistrationRequest1;

    private static final Long USER_ID_1 = 1L;
    private static final String USER_EMAIL_1 = "jane@gmail.com";
    private static final String USER_FIRST_NAME_1 = "Jane";
    private static final String USER_LAST_NAME_1 = "Doe";
    private static final String NON_EXISTENT_EMAIL = "unknown@example.com";


    @BeforeEach
    void setUp() {
        userRegistrationRequest1 = new UserRegistrationRequest(
                USER_EMAIL_1,
                USER_FIRST_NAME_1,
                USER_LAST_NAME_1
        );

        user1 = User.builder()
                .userId(USER_ID_1)
                .email(USER_EMAIL_1)
                .firstName(USER_FIRST_NAME_1)
                .lastName(USER_LAST_NAME_1)
                .registrationDate(LocalDateTime.now())
                .build();
    }


    // registerUser

    @Test
    void registerUser_ReturnsCreatedUser_WhenValidAndUnique() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user1);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequest1)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/users/" + user1.getUserId()))
                .andExpect(jsonPath("$.userId", is(user1.getUserId().intValue())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())))
                .andExpect(jsonPath("$.firstName", is(user1.getFirstName())));

        verify(userService, times(1)).registerUser(argThat(user ->
                user.getEmail().equals(USER_EMAIL_1) &&
                        user.getFirstName().equals(USER_FIRST_NAME_1) &&
                        user.getLastName().equals(USER_LAST_NAME_1)
        ));
    }

    @Test
    void registerUser_ReturnsConflict_WhenEmailExists() throws Exception {
        when(userService.registerUser(any(User.class)))
                .thenThrow(new DuplicateResourceException("Email " + USER_EMAIL_1 + " already exists."));

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequest1)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void registerUser_ReturnsBadRequest_WhenEmailIsNull() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(null, "Test", "User");

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("email is required for registration.",
                        ((ResponseStatusException) result.getResolvedException()).getReason()));

        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void registerUser_ReturnsBadRequest_WhenEmailIsBlank() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(" ", "Test", "User");

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("email is required for registration.",
                        ((ResponseStatusException) result.getResolvedException()).getReason()));

        verify(userService, never()).registerUser(any(User.class));
    }


    @Test
    void registerUser_ReturnsBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        String invalidJson = "{\"email\": \"test@example.com\", \"firstName\": \"Test\"";

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any(User.class));
    }

    // loginUser

    @Test
    void loginUser_ReturnsUser_WhenEmailExists() throws Exception {
        when(userService.loginUser(USER_EMAIL_1)).thenReturn(user1);

        mockMvc.perform(get("/api/v1/users/login")
                        .param("email", USER_EMAIL_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(user1.getUserId().intValue())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())))
                .andExpect(jsonPath("$.firstName", is(user1.getFirstName())));

        verify(userService, times(1)).loginUser(USER_EMAIL_1);
    }

    @Test
    void loginUser_ReturnsNotFound_WhenEmailDoesNotExist() throws Exception {
        when(userService.loginUser(NON_EXISTENT_EMAIL))
                .thenThrow(new ResourceNotFoundException("User with email '" + NON_EXISTENT_EMAIL + "' not found."));

        mockMvc.perform(get("/api/v1/users/login")
                        .param("email", NON_EXISTENT_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).loginUser(NON_EXISTENT_EMAIL);
    }

    @Test
    void loginUser_ReturnsBadRequest_WhenEmailParamIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).loginUser(anyString());
    }
}
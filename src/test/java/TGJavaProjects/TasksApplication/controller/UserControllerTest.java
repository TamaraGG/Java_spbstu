package TGJavaProjects.TasksApplication.controller;



import static org.hamcrest.Matchers.hasSize;
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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;
    private static final long USER_ID_1 = 1L;
    private static final long USER_ID_2 = 2L;
    private static final long NON_EXISTENT_USER_ID = 28L;


    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userId(USER_ID_1)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@gmail.com")
                .build();
        user2 = User.builder()
                .userId(USER_ID_2)
                .firstName("Jon")
                .lastName("Smith")
                .email("jjj@gmail.com")
                .build();

    }


    // getAllUsers

    @Test
    void getAllUsers_ReturnsListOfUsers() throws Exception {
        List<User> users = Arrays.asList(user1, user2);
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is((int) USER_ID_1)))
                .andExpect(jsonPath("$[0].firstName", is(user1.getFirstName())))
                .andExpect(jsonPath("$[1].userId", is((int) USER_ID_2)))
                .andExpect(jsonPath("$[1].firstName", is(user2.getFirstName())));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void getAllUsers_ReturnsEmptyList() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).findAllUsers();
    }

    // getUserById

    @Test
    void getUserById_ReturnsUser_WhenExists() throws Exception {
        when(userService.findUserById(USER_ID_1)).thenReturn(user1);

        mockMvc.perform(get("/api/v1/users/{id}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(user1.getUserId().intValue())))
                .andExpect(jsonPath("$.firstName", is(user1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user1.getLastName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));

        verify(userService, times(1)).findUserById(USER_ID_1);
    }

    @Test
    void getUserById_ReturnsNotFound_WhenDoesNotExist() throws Exception {
        when(userService.findUserById(NON_EXISTENT_USER_ID))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{id}", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findUserById(NON_EXISTENT_USER_ID);
    }

    // addUser

    @Test
    void addUser_ReturnsUser_WhenValid() throws Exception {
        when(userService.addUser(any(User.class))).thenReturn(user1);
        String userJson = objectMapper.writeValueAsString(user1);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/users/" + user1.getUserId()))
                .andExpect(jsonPath("$.userId", is(user1.getUserId().intValue())))
                .andExpect(jsonPath("$.firstName", is(user1.getFirstName())));

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void addUser_ReturnsConflict_WhenIdExists() throws Exception {
        when(userService.addUser(any(User.class)))
                .thenThrow(new DuplicateResourceException("ID exists"));
        String userJson = objectMapper.writeValueAsString(user1);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict());

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void addUser_ReturnsBadRequest_WhenIllegalArgument() throws Exception {
        when(userService.addUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Invalid input"));
        String userJson = objectMapper.writeValueAsString(user1);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).addUser(any(User.class));
    }

    @Test
    void addUser_ReturnsBadRequest_WhenInvalidJson() throws Exception {
        String invalidJson = "{\"userId\": 1, \"firstName\": \"Test\",";

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(any(User.class));
    }

    // updateUser

    @Test
    void updateUser_ReturnsUpdatedUser_WhenValid() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(user1);
        String userJson = objectMapper.writeValueAsString(user1);

        mockMvc.perform(put("/api/v1/users/{id}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(user1.getUserId().intValue())))
                .andExpect(jsonPath("$.firstName", is(user1.getFirstName())));

        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    void updateUser_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        when(userService.updateUser(any(User.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));
        String userJson = objectMapper.writeValueAsString(user1);

        mockMvc.perform(put("/api/v1/users/{id}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    void updateUser_ReturnsBadRequest_WhenIdMismatch() throws Exception {
        String userJson = objectMapper.writeValueAsString(user1);

        mockMvc.perform(put("/api/v1/users/{id}", USER_ID_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(User.class));
    }

    @Test
    void updateUser_ReturnsBadRequest_WhenIllegalArgument() throws Exception {
        when(userService.updateUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));
        String userJson = objectMapper.writeValueAsString(user1);

        mockMvc.perform(put("/api/v1/users/{id}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).updateUser(any(User.class));
    }

    // deleteUser

    @Test
    void deleteUser_ReturnsNoContent_WhenSuccessful() throws Exception {
        doNothing().when(userService).deleteUser(USER_ID_1);

        mockMvc.perform(delete("/api/v1/users/{id}", USER_ID_1))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(USER_ID_1);
    }

    @Test
    void deleteUser_ReturnsNotFound_WhenUserDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(NON_EXISTENT_USER_ID);

        mockMvc.perform(delete("/api/v1/users/{id}", NON_EXISTENT_USER_ID))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(NON_EXISTENT_USER_ID);
    }
}
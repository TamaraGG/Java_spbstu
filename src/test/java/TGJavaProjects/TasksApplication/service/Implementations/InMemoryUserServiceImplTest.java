package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryUserServiceImplTest {

    @InjectMocks
    private InMemoryUserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user1;
    private User userToRegister;

    private static final long USER_ID_1 = 1L;
    private static final String USER_EMAIL_1 = "test@example.com";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";
    private static final long NON_EXISTENT_USER_ID = 9L;

    @BeforeEach
    void setUp() {
        userToRegister = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email(USER_EMAIL_1)
                .registrationDate(LocalDateTime.now())
                .build();

        user1 = User.builder()
                .userId(USER_ID_1)
                .firstName("Jane")
                .lastName("Doe")
                .email(USER_EMAIL_1)
                .registrationDate(userToRegister.getRegistrationDate())
                .build();
    }

    // findAllUsers

    @Test
    void findAllUsers_ReturnsListOfUsers() {
        List<User> expectedUsers = List.of(user1);
        when(userRepository.findAllUsers()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAllUsers();

        assertNotNull(actualUsers);
        assertEquals(expectedUsers, actualUsers);
        assertEquals(1, actualUsers.size());
        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    void findAllUsers_ReturnsEmptyList_WhenRepositoryReturnsEmpty() {
        when(userRepository.findAllUsers()).thenReturn(Collections.emptyList());
        List<User> actualUsers = userService.findAllUsers();
        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());
        verify(userRepository, times(1)).findAllUsers();
    }

    // registerUser

    @Test
    void registerUser_ReturnsUser_WhenSuccessful() {
        when(userRepository.saveUser(userToRegister)).thenReturn(user1);

        User registeredUser = userService.registerUser(userToRegister);

        assertNotNull(registeredUser);
        assertEquals(user1, registeredUser);
        assertNotNull(registeredUser.getUserId());
        verify(userRepository, times(1)).saveUser(userToRegister);
    }

    @Test
    void registerUser_ThrowsDuplicateResourceException_WhenRepositoryThrows() {
        when(userRepository.saveUser(userToRegister))
                .thenThrow(new DuplicateResourceException("Email exists"));

        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(userToRegister));
        verify(userRepository, times(1)).saveUser(userToRegister);
    }

    @Test
    void registerUser_ThrowsIllegalArgumentException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(null)
        );
        assertEquals("user and email cannot be null for registration", exception.getMessage());
        verify(userRepository, never()).saveUser(any());
    }

    // findUserById

    @Test
    void findUserById_ReturnsUser_WhenFound() {
        when(userRepository.findUserById(USER_ID_1)).thenReturn(Optional.of(user1));
        User foundUser = userService.findUserById(USER_ID_1);
        assertNotNull(foundUser);
        assertEquals(user1, foundUser);
        verify(userRepository, times(1)).findUserById(USER_ID_1);
    }

    @Test
    void findUserById_ThrowsResourceNotFoundException_WhenNotFound() {
        when(userRepository.findUserById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserById(NON_EXISTENT_USER_ID)
        );
        assertTrue(exception.getMessage()
                .contains("user " + NON_EXISTENT_USER_ID + " not found"));
        verify(userRepository, times(1)).findUserById(NON_EXISTENT_USER_ID);
    }

    // loginUser
    @Test
    void loginUser_ReturnsUser_WhenEmailExists() {
        when(userRepository.findByEmail(USER_EMAIL_1)).thenReturn(Optional.of(user1));
        User loggedInUser = userService.loginUser(USER_EMAIL_1);
        assertNotNull(loggedInUser);
        assertEquals(user1, loggedInUser);
        verify(userRepository, times(1)).findByEmail(USER_EMAIL_1);
    }

    @Test
    void loginUser_ThrowsResourceNotFoundException_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail(NON_EXISTENT_EMAIL)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.loginUser(NON_EXISTENT_EMAIL)
        );
        assertTrue(exception.getMessage()
                .contains("user with email '" + NON_EXISTENT_EMAIL + "' not found"));
        verify(userRepository, times(1)).findByEmail(NON_EXISTENT_EMAIL);
    }

    // deleteUser
    @Test
    void deleteUser_CompletesNormally_WhenSuccessful() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(userRepository.deleteUser(USER_ID_1)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(USER_ID_1));

        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(userRepository, times(1)).deleteUser(USER_ID_1);
    }

    @Test
    void deleteUser_ThrowsResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(NON_EXISTENT_USER_ID)
        );
        assertTrue(exception.getMessage()
                .contains("delete error. user with id " + NON_EXISTENT_USER_ID + " not found"));
        verify(userRepository, times(1)).existsById(NON_EXISTENT_USER_ID);
        verify(userRepository, never()).deleteUser(anyLong());
    }

    @Test
    void deleteUser_ThrowsRuntimeException_WhenRepositoryDeleteFails() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(userRepository.deleteUser(USER_ID_1)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.deleteUser(USER_ID_1)
        );
        assertTrue(exception.getMessage().contains("delete failed for user " + USER_ID_1));
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(userRepository, times(1)).deleteUser(USER_ID_1);
    }

}


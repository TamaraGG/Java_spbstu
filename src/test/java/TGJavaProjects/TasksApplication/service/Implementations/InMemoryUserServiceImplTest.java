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
                .build();

        user1 = User.builder()
                .userId(USER_ID_1)
                .firstName("Jane")
                .lastName("Doe")
                .email(USER_EMAIL_1)
                .registrationDate(LocalDateTime.now())
                .build();
    }

    // findAll

    @Test
    void findAllUsers_ReturnsListOfUsers() {
        List<User> expectedUsers = List.of(user1);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAllUsers();

        assertNotNull(actualUsers);
        assertEquals(expectedUsers, actualUsers);
        assertEquals(1, actualUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAllUsers_ReturnsEmptyList_WhenRepositoryReturnsEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<User> actualUsers = userService.findAllUsers();
        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    // registerUser

    @Test
    void registerUser_ReturnsUser_WhenSuccessful() {
        when(userRepository.existsByEmail(userToRegister.getEmail())).thenReturn(false);
        when(userRepository.save(userToRegister)).thenReturn(user1);

        User registeredUser = userService.registerUser(userToRegister);

        assertNotNull(registeredUser);
        assertEquals(user1, registeredUser);
        assertNotNull(registeredUser.getUserId());
        verify(userRepository, times(1))
                .existsByEmail(userToRegister.getEmail());
        verify(userRepository, times(1))
                .save(userToRegister);
    }

    @Test
    void registerUser_ThrowsDuplicateResourceException_WhenEmailExists() {
        when(userRepository.existsByEmail(userToRegister.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.registerUser(userToRegister)
        );
        assertTrue(exception.getMessage()
                .contains("user with email " + userToRegister.getEmail() + " already exists."));

        verify(userRepository, times(1))
                .existsByEmail(userToRegister.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ThrowsIllegalArgumentException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(null)
        );
        assertEquals("user cannot be null for registration", exception.getMessage());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // findUserById

    @Test
    void findUserById_ReturnsUser_WhenFound() {
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user1));
        User foundUser = userService.findUserById(USER_ID_1);
        assertNotNull(foundUser);
        assertEquals(user1, foundUser);
        verify(userRepository, times(1)).findById(USER_ID_1);
    }

    @Test
    void findUserById_ThrowsResourceNotFoundException_WhenNotFound() {
        when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserById(NON_EXISTENT_USER_ID)
        );
        assertTrue(exception.getMessage()
                .contains("user " + NON_EXISTENT_USER_ID + " not found"));
        verify(userRepository, times(1)).findById(NON_EXISTENT_USER_ID);
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
        doNothing().when(userRepository).deleteById(USER_ID_1);

        assertDoesNotThrow(() -> userService.deleteUser(USER_ID_1));

        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(userRepository, times(1)).deleteById(USER_ID_1);
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
        verify(userRepository, never()).deleteById(anyLong());
    }

}


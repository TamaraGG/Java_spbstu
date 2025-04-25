package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Profile("!inmemory")
class UserServiceH2ImplTest {

    @InjectMocks
    private JpaUserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user1;
    private User userToCreate;
    private User user1Updated;
    private static final long USER_ID_1 = 1L;
    private static final long NON_EXISTENT_USER_ID = 9L;

    @BeforeEach
    void setUp() {
        userToCreate = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@test.com")
                .build();

        user1 = User.builder()
                .userId(USER_ID_1)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@test.com")
                .build();

        user1Updated = User.builder()
                .userId(USER_ID_1)
                .firstName("Janet")
                .lastName("Doe")
                .email("janet.doe@test.com")
                .build();
    }

    // findAllUsers

    @Test
    void findAllUsers_ShouldReturnListOfUsers_WhenUsersExist() {
        List<User> expectedUsers = List.of(user1);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAllUsers();

        assertNotNull(actualUsers);
        assertEquals(expectedUsers, actualUsers);
        assertEquals(1, actualUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> actualUsers = userService.findAllUsers();

        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    // findUserById

    @Test
    void findUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user1));

        User foundUser = userService.findUserById(USER_ID_1);

        assertNotNull(foundUser);
        assertEquals(user1, foundUser);
        verify(userRepository, times(1)).findById(USER_ID_1);
    }

    @Test
    void findUserById_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserById(NON_EXISTENT_USER_ID)
        );

        assertTrue(exception.getMessage().contains("user with id " + NON_EXISTENT_USER_ID + " not found"));
        verify(userRepository, times(1)).findById(NON_EXISTENT_USER_ID);
    }

    // addUser

    @Test
    void addUser_ShouldReturnSavedUser_WhenUserIsValid() {
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User addedUser = userService.addUser(userToCreate);

        assertNotNull(addedUser);
        assertEquals(USER_ID_1, addedUser.getUserId());
        assertEquals(userToCreate.getEmail(), addedUser.getEmail());
        verify(userRepository, times(1)).save(userToCreate);
    }

    @Test
    void addUser_ShouldThrowIllegalArgumentException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(null)
        );
        assertEquals("user cannot be null", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addUser_ShouldThrowIllegalArgumentException_WhenUserIdIsNotNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(user1)
        );
        assertEquals("user id must be null to create new user", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addUser_ShouldThrowIllegalArgumentException_WhenEmailIsBlank() {
        userToCreate.setEmail(" ");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(userToCreate)
        );
        assertEquals("user email cannot be blank", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addUser_ShouldThrowIllegalArgumentException_WhenFirstNameIsBlank() {
        userToCreate.setFirstName(null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(userToCreate)
        );
        assertEquals("user first name cannot be blank", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addUser_ShouldThrowIllegalArgumentException_WhenLastNameIsBlank() {
        userToCreate.setLastName("");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(userToCreate)
        );
        assertEquals("user last name cannot be blank", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addUser_ShouldRethrowException_WhenRepositorySaveFails() {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Simulated DB error"));

        assertThrows(DataIntegrityViolationException.class, () -> userService.addUser(userToCreate));
        verify(userRepository, times(1)).save(userToCreate);
    }


    // updateUser

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenUserIsValidAndExists() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user1Updated);

        User result = userService.updateUser(user1Updated);

        assertNotNull(result);
        assertEquals(user1Updated.getFirstName(), result.getFirstName());
        assertEquals(user1Updated.getEmail(), result.getEmail());
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(userRepository, times(1)).save(user1Updated);
    }

    @Test
    void updateUser_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        User nonExistentUser = User.builder().userId(NON_EXISTENT_USER_ID).firstName("Ghost").lastName("User").email("ghost@test.com").build();

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(nonExistentUser)
        );
        assertTrue(exception.getMessage().contains("update error. user with id " + NON_EXISTENT_USER_ID + " not found"));
        verify(userRepository, times(1)).existsById(NON_EXISTENT_USER_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldThrowIllegalArgumentException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(null)
        );
        assertEquals("user cannot be null", exception.getMessage());
        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldThrowIllegalArgumentException_WhenUserIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(userToCreate)
        );
        assertEquals("user id cannot be null to update", exception.getMessage());
        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldRethrowException_WhenRepositorySaveFails() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Simulated DB error"));

        assertThrows(DataIntegrityViolationException.class, () -> userService.updateUser(user1Updated));
        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(userRepository, times(1)).save(user1Updated);
    }

    // deleteUser

    @Test
    void deleteUser_ShouldCompleteSuccessfully_WhenUserExists() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(USER_ID_1);

        assertDoesNotThrow(() -> userService.deleteUser(USER_ID_1));

        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(userRepository, times(1)).deleteById(USER_ID_1);
    }

    @Test
    void deleteUser_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(NON_EXISTENT_USER_ID)
        );
        assertTrue(exception.getMessage().contains("delete error. user with id " + NON_EXISTENT_USER_ID + " not found"));
        verify(userRepository, times(1)).existsById(NON_EXISTENT_USER_ID);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_ShouldHandleException_WhenRepositoryDeleteThrows() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        doThrow(new RuntimeException("Simulated delete error")).when(userRepository).deleteById(USER_ID_1);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(USER_ID_1));

        verify(userRepository, times(1)).existsById(USER_ID_1);
        verify(userRepository, times(1)).deleteById(USER_ID_1);
    }
}
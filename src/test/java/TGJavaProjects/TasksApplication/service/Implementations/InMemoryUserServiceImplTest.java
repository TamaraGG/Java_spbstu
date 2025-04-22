package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.Notification;
import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.InMemoryNotificationDAO;
import TGJavaProjects.TasksApplication.repository.InMemoryUserDAO;
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
    private InMemoryUserDAO userDAO;

    private User user1;
    private User user1Updated;
    private static final long USER_ID_1 = 1L;
    private static final long NON_EXISTENT_USER_ID = 9L;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userId(USER_ID_1)
                .firstName("Jane")
                .lastName("Doe")
                .email("test@gmail.com")
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
    void findAllUsers_ReturnsListOfUsers() {
        List<User> expectedUsers = List.of(user1);
        when(userDAO.findAllUsers()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAllUsers();

        assertNotNull(actualUsers);
        assertEquals(expectedUsers, actualUsers);
        assertEquals(1, actualUsers.size());
        verify(userDAO, times(1)).findAllUsers();
    }

    @Test
    void findAllUsers_ReturnsEmptyList() {
        when(userDAO.findAllUsers()).thenReturn(Collections.emptyList());

        List<User> actualUsers = userService.findAllUsers();

        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());
        verify(userDAO, times(1)).findAllUsers();
    }

    // findUserById

    @Test
    void findUserById_ReturnsUser_WhenFound() {
        when(userDAO.findUserById(USER_ID_1)).thenReturn(Optional.of(user1));

        User foundUser = userService.findUserById(USER_ID_1);

        assertNotNull(foundUser);
        assertEquals(user1, foundUser);
        verify(userDAO, times(1)).findUserById(USER_ID_1);
    }

    @Test
    void findUserById_ThrowsNotFound_WhenNotFound() {
        when(userDAO.findUserById(NON_EXISTENT_USER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserById(NON_EXISTENT_USER_ID)
        );

        assertTrue(exception.getMessage().contains("user " + NON_EXISTENT_USER_ID + " not found"));
        verify(userDAO, times(1)).findUserById(NON_EXISTENT_USER_ID);
    }

    // addUser

    @Test
    void addUser_ReturnsUser_WhenSuccessful() {
        when(userDAO.addUser(user1)).thenReturn(user1);

        User addedUser = userService.addUser(user1);

        assertNotNull(addedUser);
        assertEquals(user1, addedUser);
        verify(userDAO, times(1)).addUser(user1);
    }

    @Test
    void addUser_ThrowsDuplicateException_WhenDaoThrows() {
        when(userDAO.addUser(user1)).thenThrow(new DuplicateResourceException("ID exists"));

        assertThrows(DuplicateResourceException.class, () -> userService.addUser(user1));
        verify(userDAO, times(1)).addUser(user1);
    }

    @Test
    void addUser_ThrowsIllegalArgument_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(null)
        );
        assertEquals("user cannot be null", exception.getMessage());
        verify(userDAO, never()).addUser(any());
    }

    // updateUser

    @Test
    void updateUser_ReturnsUpdatedUser_WhenSuccessful() {
        when(userDAO.existsById(USER_ID_1)).thenReturn(true);
        when(userDAO.updateUser(user1Updated)).thenReturn(Optional.of(user1Updated));

        User result = userService.updateUser(user1Updated);

        assertNotNull(result);
        assertEquals(user1Updated, result);
        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(userDAO, times(1)).updateUser(user1Updated);
    }

    @Test
    void updateUser_ThrowsNotFound_WhenUserDoesNotExist() {
        when(userDAO.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);
        User nonExistentUser = User.builder().userId(NON_EXISTENT_USER_ID).firstName("Ghost").build();

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(nonExistentUser)
        );
        assertTrue(exception.getMessage().contains("update error. user with id " + NON_EXISTENT_USER_ID + " not found"));
        verify(userDAO, times(1)).existsById(NON_EXISTENT_USER_ID);
        verify(userDAO, never()).updateUser(any());
    }

    @Test
    void updateUser_ThrowsIllegalArgument_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(null)
        );
        assertEquals("user cannot be null", exception.getMessage());
        verify(userDAO, never()).existsById(anyLong());
        verify(userDAO, never()).updateUser(any());
    }

    @Test
    void updateUser_ThrowsRuntimeException_WhenDaoUpdateFails() {

        when(userDAO.existsById(USER_ID_1)).thenReturn(true);
        when(userDAO.updateUser(user1Updated)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.updateUser(user1Updated)
        );
        assertTrue(exception.getMessage().contains("update failed for user " + USER_ID_1));
        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(userDAO, times(1)).updateUser(user1Updated);
    }

    // deleteUser

    @Test
    void deleteUser_CompletesNormally_WhenSuccessful() {
        when(userDAO.existsById(USER_ID_1)).thenReturn(true);
        when(userDAO.deleteUser(USER_ID_1)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(USER_ID_1));

        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(userDAO, times(1)).deleteUser(USER_ID_1);
    }

    @Test
    void deleteUser_ThrowsNotFound_WhenUserDoesNotExist() {
        when(userDAO.existsById(NON_EXISTENT_USER_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(NON_EXISTENT_USER_ID)
        );
        assertTrue(exception.getMessage().contains("delete error. user with id " + NON_EXISTENT_USER_ID + " not found"));
        verify(userDAO, times(1)).existsById(NON_EXISTENT_USER_ID);
        verify(userDAO, never()).deleteUser(anyLong());
    }

    @Test
    void deleteUser_ThrowsRuntimeException_WhenDaoDeleteFails() {
        when(userDAO.existsById(USER_ID_1)).thenReturn(true);
        when(userDAO.deleteUser(USER_ID_1)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.deleteUser(USER_ID_1)
        );
        assertTrue(exception.getMessage().contains("delete failed for user " + USER_ID_1));
        verify(userDAO, times(1)).existsById(USER_ID_1);
        verify(userDAO, times(1)).deleteUser(USER_ID_1);
    }

}


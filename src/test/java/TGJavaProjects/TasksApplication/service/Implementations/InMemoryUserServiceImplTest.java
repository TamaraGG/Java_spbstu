package TGJavaProjects.TasksApplication.service.Implementations;

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

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "Jane",
                "Doe",
                "test@gmail.com"
        );
    }


    @Test
    void findAllUsers_ReturnsListOfUsers_WhenNotEmpty() {

        when(userDAO.findAllUsers())
                .thenReturn(List.of(user));

        List<User> result = userService.findAllUsers();

        assertEquals(1, result.size());
        verify(userDAO, times(1)).findAllUsers();

    }

    @Test
    void findAllUsers_ReturnsListOfUsers_WhenEmpty() {

        when(userDAO.findAllUsers())
                .thenReturn(List.of());

        List<User> result = userService.findAllUsers();

        assertEquals(0, result.size());
        verify(userDAO, times(1)).findAllUsers();

    }

    @Test
    void addUser_ReturnsUser_WhenIdIsValid() {
        when(userDAO.existsById(user.getUserId()))
                .thenReturn(false);
        when(userDAO.addUser(user))
                .thenReturn(user);

        Optional<User> result = userService.addUser(user);

        assert(result.isPresent());
        assertEquals(user, result.get());
        verify(userDAO, times(1)).addUser(user);

    }

    @Test
    void findUserById_ReturnsUser_WhenUserExists() {

        when(userDAO.findUserById(user.getUserId()))
                .thenReturn(user);

        Optional<User> result = userService.findUserById(user.getUserId());

        assert(result.isPresent());
        assertEquals(user, result.get());
        verify(userDAO, times(1)).findUserById(user.getUserId());


    }

    @Test
    void findUserById_ReturnsUser_WhenUserDoesNotExist() {

        when(userDAO.findUserById(user.getUserId()))
                .thenReturn(null);

        Optional<User> result = userService.findUserById(user.getUserId());

        assert(result.isEmpty());
        verify(userDAO, times(1)).findUserById(user.getUserId());


    }

    @Test
    void updateUser_ReturnsUpdatedUser() {

        when(userDAO.updateUser(user))
                .thenReturn(user);

        Optional<User> result = userService.updateUser(user);

        assert(result.isPresent());
        assertEquals(user, result.get());
        verify(userDAO, times(1)).updateUser(user);
    }

    @Test
    void deleteUser_ReturnDeletedUser_WhenUserExists() {
        when(userDAO.deleteUser(user.getUserId()))
                .thenReturn(user);

        Optional<User> result = userService.deleteUser(user.getUserId());

        assert(result.isPresent());
        assertEquals(user, result.get());
        verify(userDAO, times(1)).deleteUser(user.getUserId());
    }

    @Test
    void deleteUser_ReturnDeletedUser_WhenUserDoesNotExist() {
        when(userDAO.deleteUser(user.getUserId()))
                .thenReturn(null);

        Optional<User> result = userService.deleteUser(user.getUserId());

        assert(result.isEmpty());
        verify(userDAO, times(1)).deleteUser(user.getUserId());
    }

}


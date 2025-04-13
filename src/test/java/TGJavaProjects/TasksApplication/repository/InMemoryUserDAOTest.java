package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryUserDAOTest {

    private InMemoryUserDAO repository;

    private static final User user1 = mock(User.class);
    private static final User user2 = mock(User.class);

    private static final long userId = 1L;


    @BeforeEach
    void setUp() {
        repository = new InMemoryUserDAO();
    }

    @Test
    void findAllUsers_ReturnsListOfUsers_WhenNotEmpty() {
        repository.addUser(user1);
        repository.addUser(user2);

        List<User> receivedUsers = repository.findAllUsers();

        assertNotNull(receivedUsers);
        assertEquals(2, receivedUsers.size());
    }

    @Test
    void findAllUsers_ReturnsListOfUsers_WhenEmpty() {

        List<User> receivedUsers = repository.findAllUsers();

        assertNotNull(receivedUsers);
        assertEquals(0, receivedUsers.size());
    }

    @Test
    void addUser_ReturnsAddedUser_WhenNotNull() {
        when(user1.getUserId()).thenReturn(userId);

        User addedUser = repository.addUser(user1);

        assertNotNull(addedUser);
        assertEquals(user1, addedUser);
    }

    @Test
    void addUser_ReturnsAddedUser_WhenNull() {
        repository.addUser(user1);

        User addedUser = repository.addUser(null);

        assertNull(addedUser);
        assertEquals(1, repository.findAllUsers().size());
    }

    @Test
    void findUserById_ReturnsUser_WhenExists() {
        when(user1.getUserId()).thenReturn(userId);
        repository.addUser(user1);

        User foundUser = repository.findUserById(userId);

        assertNotNull(foundUser);
        assertEquals(user1, foundUser);
    }

    @Test
    void findUserById_ReturnsUser_WhenDoesNotExist() {
        when(user1.getUserId()).thenReturn(userId);
        repository.addUser(user1);

        User foundUser = repository.findUserById(userId + 1);

        assertNull(foundUser);
    }

    @Test
    void updateUser_ReturnsUpdatedUser_WhenExists() {
        when(user1.getUserId()).thenReturn(userId);
        when(user2.getUserId()).thenReturn(userId);
        repository.addUser(user1);

        User updatedUser = repository.updateUser(user2);

        assertNotNull(updatedUser);
        assertEquals(user2, updatedUser);
        assertEquals(1, repository.findAllUsers().size());
    }

    @Test
    void updateUser_ReturnsUpdatedUser_WhenDoesNotExist() {
        when(user1.getUserId()).thenReturn(userId);
        when(user2.getUserId()).thenReturn(userId + 1);
        repository.addUser(user1);

        User updatedUser = repository.updateUser(user2);

        assertNull(updatedUser);
        assertEquals(1, repository.findAllUsers().size());
    }

    @Test
    void deleteUser_ReturnsDeletedUser_WhenExists() {
        when(user1.getUserId()).thenReturn(userId);
        when(user2.getUserId()).thenReturn(userId + 1);
        repository.addUser(user1);
        repository.addUser(user2);

        User deletedUser = repository.deleteUser(userId);

        assertNotNull(deletedUser);
        assertEquals(user1, deletedUser);
        assertEquals(1, repository.findAllUsers().size());
    }

    @Test
    void deleteUser_ReturnsDeletedUser_WhenDoesNotExist() {
        when(user1.getUserId()).thenReturn(userId);
        repository.addUser(user1);

        User deletedUser = repository.deleteUser(userId + 1);

        assertNull(deletedUser);
        assertEquals(1, repository.findAllUsers().size());
    }
}
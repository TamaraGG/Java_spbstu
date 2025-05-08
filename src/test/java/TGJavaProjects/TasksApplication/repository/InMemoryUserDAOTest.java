package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryUserDAOTest {

    private InMemoryUserDAO repository;

    private static final User USER_1 = User.builder()
            .userId(1L)
            .firstName("Jane")
            .lastName("Doe")
            .email("jane@gmail.com")
            .build();

    private static final User USER_2 = User.builder()
            .userId(2L)
            .firstName("Jon")
            .lastName("Smith")
            .email("jjj@gmail.com")
            .build();

    private static final User USER_1_UPDATED = User.builder()
            .userId(1L)
            .firstName("Kate")
            .lastName("Doe")
            .email("kate@gmail.com")
            .build();



    @BeforeEach
    void setUp() {
        repository = new InMemoryUserDAO();
    }

    // findAllUsers

    @Test
    void findAllUsers_ReturnsListOfUsers_WhenNotEmpty() {
        repository.addUser(USER_1);
        repository.addUser(USER_2);

        List<User> receivedUsers = repository.findAllUsers();

        assertNotNull(receivedUsers);
        assertEquals(2, receivedUsers.size());
        assertTrue(receivedUsers.contains(USER_1));
        assertTrue(receivedUsers.contains(USER_2));
    }

    @Test
    void findAllUsers_ReturnsEmptyList_WhenEmpty() {

        List<User> receivedUsers = repository.findAllUsers();

        assertNotNull(receivedUsers);
        assertTrue(receivedUsers.isEmpty());

    }

    // addUser

    @Test
    void addUser_ReturnsAddedUser_WhenValidAndUnique() {

        User addedUser = repository.addUser(USER_1);

        assertNotNull(addedUser);
        assertEquals(USER_1, addedUser);
        assertEquals(1, repository.findAllUsers().size());
        assertTrue(repository.existsById(USER_1.getUserId()));
        assertEquals(Optional.of(USER_1), repository.findUserById(USER_1.getUserId()));
    }

    @Test
    void addUser_ThrowsDuplicateResourceException_WhenIdExists() {
        repository.addUser(USER_1);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> repository.addUser(USER_1_UPDATED)
        );

        assertTrue(exception.getMessage().contains("user id 1 already exists"));
        assertEquals(1, repository.findAllUsers().size());
    }

    @Test
    void addUser_ThrowsIllegalArgumentException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.addUser(null)
        );
        assertEquals("user cannot be null", exception.getMessage());
        assertTrue(repository.findAllUsers().isEmpty());
    }

    // findUserById

    @Test
    void findUserById_ReturnsOptionalWithUser_WhenExists() {
        repository.addUser(USER_1);

        Optional<User> foundUserOpt = repository.findUserById(USER_1.getUserId());

        assertTrue(foundUserOpt.isPresent());
        assertEquals(USER_1, foundUserOpt.get());
    }

    @Test
    void findUserById_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.addUser(USER_1);

        Optional<User> foundUserOpt = repository.findUserById(USER_2.getUserId());

        assertTrue(foundUserOpt.isEmpty());
    }

    // updateUser

    @Test
    void updateUser_ReturnsOptionalWithUpdatedUser_WhenExists() {
        repository.addUser(USER_1);

        Optional<User> updatedUserOpt = repository.updateUser(USER_1_UPDATED);

        assertTrue(updatedUserOpt.isPresent());
        assertEquals(USER_1_UPDATED, updatedUserOpt.get());
        assertEquals(1, repository.findAllUsers().size());

        Optional<User> storedUserOpt = repository.findUserById(USER_1.getUserId());
        assertTrue(storedUserOpt.isPresent());
        assertEquals(USER_1_UPDATED, storedUserOpt.get());
    }

    @Test
    void updateUser_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.addUser(USER_1);

        Optional<User> updatedUserOpt = repository.updateUser(USER_2);

        assertTrue(updatedUserOpt.isEmpty());
        assertEquals(1, repository.findAllUsers().size());

        Optional<User> storedUserOpt = repository.findUserById(USER_1.getUserId());
        assertTrue(storedUserOpt.isPresent());
        assertEquals(USER_1, storedUserOpt.get());
    }

    // deleteUser

    @Test
    void deleteUser_ReturnsTrueAndRemovesUser_WhenExists() {
        repository.addUser(USER_1);
        repository.addUser(USER_2);
        assertEquals(2, repository.findAllUsers().size());

        boolean result = repository.deleteUser(USER_1.getUserId());

        assertTrue(result);
        assertEquals(1, repository.findAllUsers().size());
        assertFalse(repository.existsById(USER_1.getUserId()));
        assertTrue(repository.existsById(USER_2.getUserId()));
    }

    @Test
    void deleteUser_ReturnsFalse_WhenDoesNotExist() {
        repository.addUser(USER_1);
        assertEquals(1, repository.findAllUsers().size());

        boolean result = repository.deleteUser(USER_2.getUserId());

        assertFalse(result);
        assertEquals(1, repository.findAllUsers().size());
        assertTrue(repository.existsById(USER_1.getUserId()));
    }

    // existsById

    @Test
    void existsById_ReturnsTrue_WhenExists() {
        repository.addUser(USER_1);
        assertTrue(repository.existsById(USER_1.getUserId()));
    }

    @Test
    void existsById_ReturnsFalse_WhenDoesNotExist() {
        repository.addUser(USER_1);
        assertFalse(repository.existsById(USER_2.getUserId()));
    }

    @Test
    void existsById_ReturnsFalse_WhenEmpty() {
        assertFalse(repository.existsById(USER_1.getUserId()));
    }
}
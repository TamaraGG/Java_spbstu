package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryUserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class InMemoryUserRepositoryImplTest {

    private InMemoryUserRepositoryImpl repository;

    private User userToSave1;
    private User userToSave2;
    private User userToSaveWithSameEmailAs1;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepositoryImpl();

        userToSave1 = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .registrationDate(LocalDateTime.now())
                .build();

        userToSave2 = User.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .registrationDate(LocalDateTime.now().minusDays(1))
                .build();

        userToSaveWithSameEmailAs1 = User.builder()
                .firstName("Janet")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .registrationDate(LocalDateTime.now())
                .build();
    }

    // findAllUsers

    @Test
    void findAllUsers_ReturnsListOfUsers_WhenNotEmpty() {
        User savedUser1 = repository.saveUser(userToSave1);
        User savedUser2 = repository.saveUser(userToSave2);

        List<User> receivedUsers = repository.findAllUsers();

        assertNotNull(receivedUsers);
        assertEquals(2, receivedUsers.size());
        assertTrue(receivedUsers.stream()
                .anyMatch(u -> u.getEmail().equals(savedUser1.getEmail())));
        assertTrue(receivedUsers.stream()
                .anyMatch(u -> u.getEmail().equals(savedUser2.getEmail())));
    }

    @Test
    void findAllUsers_ReturnsEmptyList_WhenEmpty() {
        List<User> receivedUsers = repository.findAllUsers();
        assertNotNull(receivedUsers);
        assertTrue(receivedUsers.isEmpty());
    }

    // saveUser (add)

    @Test
    void saveUser_NewUser_AssignsIdAndReturnsSavedUser() {
        User savedUser = repository.saveUser(userToSave1);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserId());
        assertEquals(userToSave1.getEmail(), savedUser.getEmail());
        assertEquals(userToSave1.getFirstName(), savedUser.getFirstName());

        assertTrue(repository.existsById(savedUser.getUserId()));
        assertEquals(Optional.of(savedUser), repository.findUserById(savedUser.getUserId()));
        assertEquals(1, repository.findAllUsers().size());
    }

    @Test
    void saveUser_NewUser_ThrowsDuplicateResourceException_WhenEmailExists() {
        repository.saveUser(userToSave1);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> repository.saveUser(userToSaveWithSameEmailAs1)
        );
        assertTrue(exception.getMessage()
                .contains("user with email " + userToSave1.getEmail() + " already exists."));
        assertEquals(1, repository.findAllUsers().size());
    }

    @Test
    void saveUser_ThrowsIllegalArgumentException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.saveUser(null)
        );
        assertEquals("user cannot be null", exception.getMessage());
        assertTrue(repository.findAllUsers().isEmpty());
    }

    // saveUser (update)
    @Test
    void saveUser_ExistingUser_UpdatesAndReturnsUser() {
        User savedUser = repository.saveUser(userToSave1);
        Long originalId = savedUser.getUserId();

        User userToUpdate = User.builder()
                .userId(originalId)
                .firstName("Jane Updated")
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .registrationDate(savedUser.getRegistrationDate())
                .build();

        User updatedUser = repository.saveUser(userToUpdate);

        assertNotNull(updatedUser);
        assertEquals(originalId, updatedUser.getUserId());
        assertEquals("Jane Updated", updatedUser.getFirstName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());

        Optional<User> foundUserOpt = repository.findUserById(originalId);
        assertTrue(foundUserOpt.isPresent());
        assertEquals("Jane Updated", foundUserOpt.get().getFirstName());
        assertEquals(1, repository.findAllUsers().size());
    }

    @Test
    void saveUser_ExistingUser_UpdateEmail_ReturnsUpdatedUser_WhenNewEmailIsUnique() {
        User savedUser1 = repository.saveUser(userToSave1);
        repository.saveUser(userToSave2);

        String newUniqueEmail = "updated.jane.doe@example.com";
        User userToUpdate = User.builder()
                .userId(savedUser1.getUserId())
                .firstName(savedUser1.getFirstName())
                .lastName(savedUser1.getLastName())
                .email(newUniqueEmail)
                .registrationDate(savedUser1.getRegistrationDate())
                .build();

        User updatedUser = repository.saveUser(userToUpdate);

        assertNotNull(updatedUser);
        assertEquals(savedUser1.getUserId(), updatedUser.getUserId());
        assertEquals(newUniqueEmail, updatedUser.getEmail());
        assertEquals(2, repository.findAllUsers().size());
    }


    @Test
    void saveUser_ExistingUser_ThrowsDuplicateResourceException_WhenUpdatingToExistingEmail() {
        User savedUser1 = repository.saveUser(userToSave1);
        User savedUser2 = repository.saveUser(userToSave2);

        User userToUpdate = User.builder()
                .userId(savedUser1.getUserId())
                .firstName("Jane Updated")
                .lastName(savedUser1.getLastName())
                .email(savedUser2.getEmail())
                .registrationDate(savedUser1.getRegistrationDate())
                .build();

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> repository.saveUser(userToUpdate)
        );
        assertTrue(exception.getMessage().contains("email " + savedUser2.getEmail() + " already exists."));
        Optional<User> user1AfterAttempt = repository.findUserById(savedUser1.getUserId());
        assertTrue(user1AfterAttempt.isPresent());
        assertEquals(savedUser1.getEmail(), user1AfterAttempt.get().getEmail());
    }

    @Test
    void saveUser_ExistingUser_ThrowsIllegalArgumentException_WhenUpdatingNonExistingUser() {
        User nonExistentUserToUpdate = User.builder()
                .userId(999L)
                .firstName("Ghost")
                .lastName("User")
                .email("ghost@example.com")
                .registrationDate(LocalDateTime.now())
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.saveUser(nonExistentUserToUpdate)
        );
        assertTrue(exception.getMessage()
                .contains("cannot update non-existing user with id " + nonExistentUserToUpdate.getUserId()));
    }


    // findUserById

    @Test
    void findUserById_ReturnsOptionalWithUser_WhenExists() {
        User savedUser = repository.saveUser(userToSave1);
        Optional<User> foundUserOpt = repository.findUserById(savedUser.getUserId());
        assertTrue(foundUserOpt.isPresent());
        assertEquals(savedUser.getEmail(), foundUserOpt.get().getEmail());
    }

    @Test
    void findUserById_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.saveUser(userToSave1);
        Optional<User> foundUserOpt = repository.findUserById(999L);
        assertTrue(foundUserOpt.isEmpty());
    }

    // deleteUser

    @Test
    void deleteUser_ReturnsTrueAndRemovesUser_WhenExists() {
        User savedUser1 = repository.saveUser(userToSave1);
        User savedUser2 = repository.saveUser(userToSave2);
        assertEquals(2, repository.findAllUsers().size());

        boolean result = repository.deleteUser(savedUser1.getUserId());

        assertTrue(result);
        assertEquals(1, repository.findAllUsers().size());
        assertFalse(repository.existsById(savedUser1.getUserId()));
        assertTrue(repository.existsById(savedUser2.getUserId()));
    }

    @Test
    void deleteUser_ReturnsFalse_WhenDoesNotExist() {
        repository.saveUser(userToSave1);
        assertEquals(1, repository.findAllUsers().size());

        boolean result = repository.deleteUser(999L);

        assertFalse(result);
        assertEquals(1, repository.findAllUsers().size());
    }

    // existsById

    @Test
    void existsById_ReturnsTrue_WhenExists() {
        User savedUser = repository.saveUser(userToSave1);
        assertTrue(repository.existsById(savedUser.getUserId()));
    }

    @Test
    void existsById_ReturnsFalse_WhenDoesNotExist() {
        repository.saveUser(userToSave1);
        assertFalse(repository.existsById(999L));
    }

    // existsByEmail

    @Test
    void existsByEmail_ReturnsTrue_WhenExists() {
        User savedUser = repository.saveUser(userToSave1);
        assertTrue(repository.existsByEmail(savedUser.getEmail()));
    }

    @Test
    void existsByEmail_ReturnsTrue_WhenExistsDifferentCase() {
        User savedUser = repository.saveUser(userToSave1);
        assertTrue(repository.existsByEmail("Jane.Doe@example.com"));
    }

    @Test
    void existsByEmail_ReturnsFalse_WhenDoesNotExist() {
        repository.saveUser(userToSave1);
        assertFalse(repository.existsByEmail("nonexistent@example.com"));
    }

    // findByEmail

    @Test
    void findByEmail_ReturnsOptionalWithUser_WhenExists() {
        User savedUser = repository.saveUser(userToSave1);
        Optional<User> foundUserOpt = repository.findByEmail(savedUser.getEmail());
        assertTrue(foundUserOpt.isPresent());
        assertEquals(savedUser.getUserId(), foundUserOpt.get().getUserId());
    }

    @Test
    void findByEmail_ReturnsOptionalWithUser_WhenExistsDifferentCase() {
        User savedUser = repository.saveUser(userToSave1);
        Optional<User> foundUserOpt = repository.findByEmail("JANE.DOE@EXAMPLE.COM");
        assertTrue(foundUserOpt.isPresent());
        assertEquals(savedUser.getUserId(), foundUserOpt.get().getUserId());
    }

    @Test
    void findByEmail_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.saveUser(userToSave1);
        Optional<User> foundUserOpt = repository.findByEmail("nonexistent@example.com");
        assertTrue(foundUserOpt.isEmpty());
    }
}
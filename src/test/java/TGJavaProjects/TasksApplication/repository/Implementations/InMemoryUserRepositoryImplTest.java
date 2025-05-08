package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("in-memory")
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
                .build();
    }

    // findAll

    @Test
    void findAll_ReturnsListOfUsers_WhenNotEmpty() {
        User savedUser1 = repository.save(userToSave1);
        User savedUser2 = repository.save(userToSave2);

        List<User> receivedUsers = repository.findAll();

        assertNotNull(receivedUsers);
        assertEquals(2, receivedUsers.size());
        assertTrue(receivedUsers.stream()
                .anyMatch(u -> u.getEmail().equals(savedUser1.getEmail())));
        assertTrue(receivedUsers.stream()
                .anyMatch(u -> u.getEmail().equals(savedUser2.getEmail())));
    }

    @Test
    void findAll_ReturnsEmptyList_WhenEmpty() {
        List<User> receivedUsers = repository.findAll();
        assertNotNull(receivedUsers);
        assertTrue(receivedUsers.isEmpty());
    }

    // save

    @Test
    void save_NewUser_AssignsIdAndReturnsSavedUser() {
        User savedUser = repository.save(userToSave1);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserId());
        assertEquals(userToSave1.getEmail(), savedUser.getEmail());
        assertEquals(userToSave1.getFirstName(), savedUser.getFirstName());
        assertNotNull(savedUser.getRegistrationDate());

        assertTrue(repository.existsById(savedUser.getUserId()));
        assertEquals(Optional.of(savedUser), repository.findById(savedUser.getUserId()));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void save_NewUser_ThrowsDuplicateResourceException_WhenEmailExists() {
        repository.save(userToSave1);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> repository.save(userToSaveWithSameEmailAs1)
        );
        assertTrue(exception.getMessage()
                .contains("user with email " + userToSave1.getEmail() + " already exists."));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void save_ThrowsIllegalArgumentException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(null)
        );
        assertEquals("user cannot be null", exception.getMessage());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void save_ExistingUser_UpdatesAndReturnsUser() {
        User savedUser = repository.save(userToSave1);
        Long originalId = savedUser.getUserId();

        User userToUpdate = User.builder()
                .userId(originalId)
                .firstName("Jane Updated")
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .registrationDate(savedUser.getRegistrationDate())
                .build();

        User updatedUser = repository.save(userToUpdate);

        assertNotNull(updatedUser);
        assertEquals(originalId, updatedUser.getUserId());
        assertEquals("Jane Updated", updatedUser.getFirstName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());

        Optional<User> foundUserOpt = repository.findById(originalId);
        assertTrue(foundUserOpt.isPresent());
        assertEquals("Jane Updated", foundUserOpt.get().getFirstName());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void save_ExistingUser_UpdateEmail_ReturnsUpdatedUser_WhenNewEmailIsUnique() {
        User savedUser1 = repository.save(userToSave1);
        repository.save(userToSave2);

        String newUniqueEmail = "updated.jane.doe@example.com";
        User userToUpdate = User.builder()
                .userId(savedUser1.getUserId())
                .firstName(savedUser1.getFirstName())
                .lastName(savedUser1.getLastName())
                .email(newUniqueEmail)
                .registrationDate(savedUser1.getRegistrationDate())
                .build();

        User updatedUser = repository.save(userToUpdate);

        assertNotNull(updatedUser);
        assertEquals(savedUser1.getUserId(), updatedUser.getUserId());
        assertEquals(newUniqueEmail, updatedUser.getEmail());
        assertEquals(2, repository.findAll().size());
    }


    @Test
    void save_ExistingUser_ThrowsDuplicateResourceException_WhenUpdatingToExistingEmail() {
        User savedUser1 = repository.save(userToSave1);
        User savedUser2 = repository.save(userToSave2);

        User userToUpdate = User.builder()
                .userId(savedUser1.getUserId())
                .firstName("Jane Updated")
                .lastName(savedUser1.getLastName())
                .email(savedUser2.getEmail())
                .registrationDate(savedUser1.getRegistrationDate())
                .build();

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> repository.save(userToUpdate)
        );
        assertTrue(exception.getMessage()
                .contains("email " + savedUser2.getEmail() + " already exists"));
        Optional<User> user1AfterAttempt = repository.findById(savedUser1.getUserId());
        assertTrue(user1AfterAttempt.isPresent());
        assertEquals(savedUser1.getEmail(), user1AfterAttempt.get().getEmail());
    }

    @Test
    void save_ExistingUser_ThrowsIllegalArgumentException_WhenUpdatingNonExistingUser() {
        User nonExistentUserToUpdate = User.builder()
                .userId(999L)
                .firstName("Ghost")
                .lastName("User")
                .email("ghost@example.com")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(nonExistentUserToUpdate)
        );
        assertTrue(exception.getMessage()
                .contains("Cannot update non-existing user with id "
                        + nonExistentUserToUpdate.getUserId()));
    }


    // findById

    @Test
    void findById_ReturnsOptionalWithUser_WhenExists() {
        User savedUser = repository.save(userToSave1);
        Optional<User> foundUserOpt = repository.findById(savedUser.getUserId());
        assertTrue(foundUserOpt.isPresent());
        assertEquals(savedUser.getEmail(), foundUserOpt.get().getEmail());
    }

    @Test
    void findById_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.save(userToSave1);
        Optional<User> foundUserOpt = repository.findById(999L);
        assertTrue(foundUserOpt.isEmpty());
    }

    @Test
    void findById_ReturnsEmptyOptional_WhenIdIsNull() {
        repository.save(userToSave1);
        Optional<User> foundUserOpt = repository.findById(null);
        assertTrue(foundUserOpt.isEmpty());
    }


    // deleteById

    @Test
    void deleteById_RemovesUser_WhenExists() {
        User savedUser1 = repository.save(userToSave1);
        User savedUser2 = repository.save(userToSave2);
        assertEquals(2, repository.findAll().size());

        repository.deleteById(savedUser1.getUserId());

        assertEquals(1, repository.findAll().size());
        assertFalse(repository.existsById(savedUser1.getUserId()));
        assertTrue(repository.existsById(savedUser2.getUserId()));
    }

    @Test
    void deleteById_DoesNothing_WhenDoesNotExist() {
        User savedUser1 = repository.save(userToSave1);
        assertEquals(1, repository.findAll().size());

        repository.deleteById(999L);

        assertEquals(1, repository.findAll().size());
        assertTrue(repository.existsById(savedUser1.getUserId()));
    }

    @Test
    void deleteById_DoesNothing_WhenIdIsNull() {
        User savedUser1 = repository.save(userToSave1);
        assertEquals(1, repository.findAll().size());

        repository.deleteById(null);

        assertEquals(1, repository.findAll().size());
    }


    // existsById

    @Test
    void existsById_ReturnsTrue_WhenExists() {
        User savedUser = repository.save(userToSave1);
        assertTrue(repository.existsById(savedUser.getUserId()));
    }

    @Test
    void existsById_ReturnsFalse_WhenDoesNotExist() {
        repository.save(userToSave1);
        assertFalse(repository.existsById(999L));
    }

    @Test
    void existsById_ReturnsFalse_WhenIdIsNull() {
        repository.save(userToSave1);
        assertFalse(repository.existsById(null));
    }


    // existsByEmail

    @Test
    void existsByEmail_ReturnsTrue_WhenExists() {
        User savedUser = repository.save(userToSave1);
        assertTrue(repository.existsByEmail(savedUser.getEmail()));
    }

    @Test
    void existsByEmail_ReturnsTrue_WhenExistsDifferentCase() {
        User savedUser = repository.save(userToSave1);
        assertTrue(repository.existsByEmail("Jane.Doe@example.com"));
    }

    @Test
    void existsByEmail_ReturnsFalse_WhenDoesNotExist() {
        repository.save(userToSave1);
        assertFalse(repository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void existsByEmail_ReturnsFalse_WhenEmailIsNull() {
        repository.save(userToSave1);
        assertFalse(repository.existsByEmail(null));
    }


    // findByEmail

    @Test
    void findByEmail_ReturnsOptionalWithUser_WhenExists() {
        User savedUser = repository.save(userToSave1);
        Optional<User> foundUserOpt = repository.findByEmail(savedUser.getEmail());
        assertTrue(foundUserOpt.isPresent());
        assertEquals(savedUser.getUserId(), foundUserOpt.get().getUserId());
    }

    @Test
    void findByEmail_ReturnsOptionalWithUser_WhenExistsDifferentCase() {
        User savedUser = repository.save(userToSave1);
        Optional<User> foundUserOpt = repository.findByEmail("JANE.DOE@EXAMPLE.COM");
        assertTrue(foundUserOpt.isPresent());
        assertEquals(savedUser.getUserId(), foundUserOpt.get().getUserId());
    }

    @Test
    void findByEmail_ReturnsEmptyOptional_WhenDoesNotExist() {
        repository.save(userToSave1);
        Optional<User> foundUserOpt = repository.findByEmail("nonexistent@example.com");
        assertTrue(foundUserOpt.isEmpty());
    }

    @Test
    void findByEmail_ReturnsEmptyOptional_WhenEmailIsNull() {
        repository.save(userToSave1);
        Optional<User> foundUserOpt = repository.findByEmail(null);
        assertTrue(foundUserOpt.isEmpty());
    }
}
package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Profile("H2")
public class UserServiceH2Impl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User addUser(User user)
            throws IllegalArgumentException, DuplicateResourceException {

        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        if (user.getUserId() != null) {
            throw new IllegalArgumentException("user id must be null to create new user");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("user email cannot be blank");
        }
        if (user.getFirstName() == null || user.getFirstName().isBlank()){
            throw new IllegalArgumentException("user first name cannot be blank");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()){
            throw new IllegalArgumentException("user last name cannot be blank");
        }


        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public User findUserById(long userId)
            throws ResourceNotFoundException {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with id " + userId + " not found"));
    }

    @Override
    public User updateUser(User user)
            throws IllegalArgumentException, ResourceNotFoundException, DuplicateResourceException {

        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("user id cannot be null to update");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("user email cannot be blank");
        }
        if (user.getFirstName() == null || user.getFirstName().isBlank()){
            throw new IllegalArgumentException("user first name cannot be blank");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()){
            throw new IllegalArgumentException("user last name cannot be blank");
        }

        if (!userRepository.existsById(user.getUserId())) {
            throw new ResourceNotFoundException(
                    "update error. user with id " + user.getUserId() + " not found"
            );
        }

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public void deleteUser(long userId)
            throws ResourceNotFoundException {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "delete error. user with id " + userId + " not found");
        }

        userRepository.deleteById(userId);

    }
}

package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryUserRepositoryImpl;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor //для внедрения через конструктор
public class InMemoryUserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public User registerUser(User user) throws DuplicateResourceException {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("user and email cannot be null for registration");
        }

        return userRepository.saveUser(user);
    }

    @Override
    public User findUserById(long userId) throws ResourceNotFoundException {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user " + userId + " not found"));
    }

    @Override
    public User loginUser(String email) throws ResourceNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with email '" + email + "' not found. Cannot login."));
    }

    @Override
    public void deleteUser(long userId)
            throws RuntimeException{
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "delete error. user with id " + userId +
                            " not found");
        }

        boolean isDeleted = userRepository.deleteUser(userId);
        if (! isDeleted) {
            throw new RuntimeException("delete failed for user " + userId);
        }
    }

}

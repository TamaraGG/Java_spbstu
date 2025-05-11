package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.repository.Implementations.InMemoryUserRepositoryImpl;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class InMemoryUserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User registerUser(User user) throws DuplicateResourceException {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null for registration");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException(
                    "user with email " + user.getEmail() + " already exists.");
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(long userId) throws ResourceNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user " + userId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public User loginUser(String email) throws ResourceNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user with email '" + email + "' not found. Cannot login."));
    }

    @Override
    @Transactional
    public void deleteUser(long userId) throws ResourceNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "delete error. user with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

}

package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.repository.InMemoryUserDAO;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor //для внедрения через конструктор
public class InMemoryUserServiceImpl implements UserService {

    private final InMemoryUserDAO userDAO;

    @Override
    public List<User> findAllUsers() {
        return userDAO.findAllUsers();
    }

    @Override
    public User addUser(User user)
            throws IllegalArgumentException, DuplicateResourceException {

        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("user id cannot be null");
        }
        try {
            return userDAO.addUser(user);
        } catch (DuplicateResourceException | IllegalArgumentException e) {
            throw e;
        }

    }

    @Override
    public User findUserById(long userId) {
        return userDAO.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "user " + userId + " not found"));
    }

    @Override
    public User updateUser(User user)
            throws IllegalArgumentException, ResourceNotFoundException {

        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("user id cannot be null");
        }
        if (!userDAO.existsById(user.getUserId())) {
            throw new ResourceNotFoundException(
                    "update error. user with id " + user.getUserId() +
                    " not found"
            );
        }

        return userDAO.updateUser(user)
                .orElseThrow(() -> new RuntimeException(
                        "update failed for user " + user.getUserId()));
    }

    @Override
    public void deleteUser(long userId)
            throws ResourceNotFoundException, RuntimeException{
        if (!userDAO.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "delete error. user with id " + userId +
                            " not found");
        }

        boolean isDeleted = userDAO.deleteUser(userId);
        if (! isDeleted) {
            throw new RuntimeException("delete failed for user " + userId);
        }
    }

}

package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAllUsers();
    User saveUser(User user) throws IllegalArgumentException, DuplicateResourceException;
    Optional<User> findUserById(Long userId);
    boolean deleteUser(long userId);
    boolean existsById(Long userId);
    boolean existsByEmail(String email);
}

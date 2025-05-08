package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    User save(User user);
    Optional<User> findById(Long userId);
    void deleteById(Long userId);
    boolean existsById(Long userId);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}

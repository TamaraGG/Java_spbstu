package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    public List<User> findAllUsers();
    public User saveUser(User user) throws IllegalArgumentException, DuplicateResourceException;
    public Optional<User> findUserById(Long userId);
    public boolean deleteUser(long userId);
    public boolean existsById(Long userId);
    public boolean existsByEmail(String email);
    public Optional<User> findByEmail(String email);
}

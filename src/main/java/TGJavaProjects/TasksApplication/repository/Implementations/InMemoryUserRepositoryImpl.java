package TGJavaProjects.TasksApplication.repository.Implementations;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Repository
@Profile("in-memory")
public class InMemoryUserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public List<User> findAllUsers() {
        return List.copyOf(users);
    }

    @Override
    public User saveUser(User user)
            throws IllegalArgumentException, DuplicateResourceException {

        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        if (user.getUserId() == null) {
            if (existsByEmail(user.getEmail())) {
                throw new DuplicateResourceException(
                        "user with email " + user.getEmail() + " already exists.");
            }
            user.setUserId(idCounter.incrementAndGet());
            users.add(user);
            return user;

        } else {
            Optional<User> existingUserOpt = findUserById(user.getUserId());
            if (existingUserOpt.isPresent()) {
                users.stream()
                        .filter(u -> !u.getUserId().equals(user.getUserId()))
                        .forEach(u -> {
                            if (u.getEmail().equals(user.getEmail())) {
                                throw new DuplicateResourceException(
                                        "email " + user.getEmail() + " already exists.");
                            }
                        });
                users.removeIf(u -> u.getUserId().equals(user.getUserId()));
                users.add(user);
                return user;

            } else {
                throw new IllegalArgumentException(
                        "cannot update non-existing user with id " + user.getUserId());
            }
        }

    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public boolean deleteUser(long userId) {
        return users.removeIf(u -> u.getUserId() == userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return users.stream()
                .anyMatch(u -> u.getUserId().equals(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}

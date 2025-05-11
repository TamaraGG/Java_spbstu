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
    public List<User> findAll() {
        return List.copyOf(users);
    }

    @Override
    public User save(User user) {
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
            Optional<User> existingUserOpt = findById(user.getUserId());
            if (existingUserOpt.isPresent()) {
                users.stream()
                        .filter(u -> !u.getUserId().equals(user.getUserId()))
                        .forEach(u -> {
                            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                                throw new DuplicateResourceException(
                                        "email " + user.getEmail() + " already exists for another user.");
                            }
                        });
                users.removeIf(u -> u.getUserId().equals(user.getUserId()));
                users.add(user);
                return user;

            } else {
                throw new IllegalArgumentException(
                        "Cannot update non-existing user with id " + user.getUserId() + " via save.");
            }
        }
    }

    @Override
    public Optional<User> findById(Long userId) {
        if (userId == null) return Optional.empty();
        return users.stream()
                .filter(user -> userId.equals(user.getUserId()))
                .findFirst();
    }

    @Override
    public void deleteById(Long userId) {
        if (userId == null) return;
        users.removeIf(u -> userId.equals(u.getUserId()));
    }

    @Override
    public boolean existsById(Long userId) {
        if (userId == null) return false;
        return users.stream()
                .anyMatch(u -> userId.equals(u.getUserId()));
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return users.stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}

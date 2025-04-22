package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Repository
public class InMemoryUserDAO {

    private final List<User> users = new ArrayList<>();

    public List<User> findAllUsers() {
        return users;
    }

    public User addUser(User user)
            throws IllegalArgumentException, DuplicateResourceException {

        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (existsById(user.getUserId())) {
            throw new DuplicateResourceException(
                    "user id " + user.getUserId() + " already exists."
            );
        }
        users.add(user);
        return user;
    }

    public Optional<User> findUserById(Long userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    public Optional<User> updateUser(User user) {
        var userIndex = IntStream.range(0, users.size())
                .filter(index-> users.get(index).getUserId().equals(user.getUserId()))
                .findFirst()
                .orElse(-1);
        if (userIndex > -1) {
            users.set(userIndex, user);
            return Optional.of(user);
        }
        return Optional.empty();
    }


    public Boolean deleteUser(long userId) {
        return users.removeIf(u -> u.getUserId() == userId);
    }


    public boolean existsById(Long userId) {
        return users.stream().anyMatch(u -> u.getUserId().equals(userId));
    }
}

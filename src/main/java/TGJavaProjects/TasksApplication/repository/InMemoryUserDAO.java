package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Repository
public class InMemoryUserDAO {

    private final List<User> users = new ArrayList<>();

    public List<User> findAllUsers() {
        return users;
    }

    public User addUser(User user) {
        if (user != null && !existsById(user.getUserId())) {
            users.add(user);
        }
        return user;
    }

    public User findUserById(long userId) {
        return users.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    public User updateUser(User user) {
        var userIndex = IntStream.range(0, users.size())
                .filter(index-> users.get(index).getUserId() == user.getUserId())
                .findFirst()
                .orElse(-1);
        if (userIndex > -1) {
            users.set(userIndex, user);
            return user;
        }
        return null;
    }

    public User deleteUser(long userId) {
        var user = findUserById(userId);
        if (user != null) {
            users.remove(user);
            return user;
        }
        return null;
    }

    public boolean existsById(long userId) {
        return findUserById(userId) != null;
    }
}

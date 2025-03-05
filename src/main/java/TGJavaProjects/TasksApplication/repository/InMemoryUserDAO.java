package TGJavaProjects.TasksApplication.repository;

import TGJavaProjects.TasksApplication.model.User;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Repository
public class InMemoryUserDAO {

    private final List<User> USERS = new ArrayList<>();

    public List<User> findAllUsers() {
        return USERS;
    }

    public User addUser(User user) {
        USERS.add(user);
        return user;
    }

    public User findUserById(long userId) {
        return USERS.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    public User updateUser(User user) {
        var userIndex = IntStream.range(0, USERS.size())
                .filter(index-> USERS.get(index).getUserId() == user.getUserId())
                .findFirst()
                .orElse(-1);
        if (userIndex > -1) {
            USERS.set(userIndex, user);
            return user;
        }
        return null;
    }

    public User deleteUser(long userId) {
        var user = findUserById(userId);
        if (user != null) {
            USERS.remove(user);
        }
        return user;
    }
}

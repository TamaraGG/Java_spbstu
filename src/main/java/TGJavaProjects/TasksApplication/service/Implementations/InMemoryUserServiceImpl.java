package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.repository.InMemoryUserDAO;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.UserService;
import lombok.AllArgsConstructor;
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
    public Optional<User> addUser(User user) {
        if (!userDAO.existsById(user.getUserId())) {
            return Optional.ofNullable(userDAO.addUser(user));
        }
        return  Optional.empty();
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(userDAO.findUserById(userId));
    }

    @Override
    public Optional<User> updateUser(User user) {
        return Optional.ofNullable(userDAO.updateUser(user));
    }

    @Override
    public Optional<User> deleteUser(long userId) {
        return Optional.ofNullable(userDAO.deleteUser(userId));
    }

}

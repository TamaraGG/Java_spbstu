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

    private final InMemoryUserDAO REPOSITORY;

    @Override
    public List<User> findAllUsers() {
        return REPOSITORY.findAllUsers();
    }

    @Override
    public Optional<User> addUser(User user) {
        return Optional.ofNullable(REPOSITORY.addUser(user));
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(REPOSITORY.findUserById(userId));
    }

    @Override
    public Optional<User> updateUser(User user) {
        return Optional.ofNullable(REPOSITORY.updateUser(user));
    }

    @Override
    public Optional<User> deleteUser(long userId) {
        return Optional.ofNullable(REPOSITORY.deleteUser(userId));
    }

}

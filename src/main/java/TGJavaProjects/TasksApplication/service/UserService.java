package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    public List<User> findAllUsers();
    public Optional<User> addUser(User user);
    public Optional<User> findUserById(long userId);
    public Optional<User> updateUser (User user);
    public Optional<User> deleteUser (long userId);
}

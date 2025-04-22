package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    public List<User> findAllUsers();
    public User addUser(User user);
    public User findUserById(long userId);
    public User updateUser (User user);
    public void deleteUser (long userId);
}

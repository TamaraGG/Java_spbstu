package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    public List<User> findAllUsers();
    public User registerUser(User user) throws DuplicateResourceException;
    public User findUserById(long userId) throws ResourceNotFoundException;
    public User loginUser(String username) throws ResourceNotFoundException;
    public void deleteUser (long userId) throws ResourceNotFoundException;
}

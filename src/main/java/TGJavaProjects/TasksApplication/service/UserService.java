package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    public List<User> findAllUsers();
    public User registerUser(User user) throws DuplicateResourceException;
    public User findUserById(long userId) throws ResourceNotFoundException;
    public User loginUser(String email) throws ResourceNotFoundException;
    public void deleteUser (long userId) throws ResourceNotFoundException;
}
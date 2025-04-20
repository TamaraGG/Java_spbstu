package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.repository.UserRepository;
import TGJavaProjects.TasksApplication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("H2")
public class UserServiceH2Impl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> addUser(User user) {
        if (userRepository.findById(user.getUserId()).isEmpty()) {
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (userRepository.findById(user.getUserId()).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<User> deleteUser(long userId) {
        Optional<User> deletedUser = userRepository.findById(userId);
        userRepository.deleteById(userId);
        return deletedUser;
    }
}

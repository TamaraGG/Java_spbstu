package TGJavaProjects.TasksApplication.controller;


import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

//    @Autowired
    private final UserService USER_SERVICE;

    @GetMapping
    public List<User> findAllUsers() {
        return USER_SERVICE.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long userId) {
        return USER_SERVICE.findUserById(userId)
                .map(u-> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return USER_SERVICE.addUser(user)
                .map(u -> ResponseEntity.created(URI.create("/api/v1/users/" + u.getUserId())).body(u))
                .orElse(ResponseEntity.badRequest().build());
    }

}

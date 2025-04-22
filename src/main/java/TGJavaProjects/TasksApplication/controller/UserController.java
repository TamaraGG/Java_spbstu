package TGJavaProjects.TasksApplication.controller;


import TGJavaProjects.TasksApplication.exception.DuplicateResourceException;
import TGJavaProjects.TasksApplication.exception.ResourceNotFoundException;
import TGJavaProjects.TasksApplication.model.User;
import TGJavaProjects.TasksApplication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

//    @Autowired
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long userId)
        throws ResourceNotFoundException {

        try {
            User user = userService.findUserById(userId);
            return ResponseEntity.ok(user);

        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user)
        throws DuplicateResourceException, ResourceNotFoundException {

        try {
            User createdUser = userService.addUser(user);
            return ResponseEntity
                    .created(URI.create("/api/v1/users/" + createdUser.getUserId()))
                    .body(createdUser);
        } catch (DuplicateResourceException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") long userId, @RequestBody User user)
        throws ResourceNotFoundException, ResponseStatusException {

        if (user.getUserId() != userId) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User ID in path must match User ID in body");
        }
        try {
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long userId)
        throws ResourceNotFoundException {

        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

}

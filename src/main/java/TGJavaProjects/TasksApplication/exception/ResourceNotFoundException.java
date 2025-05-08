package TGJavaProjects.TasksApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Exception for when a requested resource is not found
@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Not Found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

package TGJavaProjects.TasksApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Exception for when a resource already exists (e.g., duplicate ID)
@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}


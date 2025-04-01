package TGJavaProjects.TasksApplication.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class Task {

    @NonNull
    private long taskId;

    @NonNull
    private String taskText;

    private LocalDateTime dueDate;

    @NonNull
    private LocalDateTime creationDate;

    @NonNull
    private boolean isComplete;

    @NonNull
    private long userId;

}

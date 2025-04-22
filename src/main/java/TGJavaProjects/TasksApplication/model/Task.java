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
    private Long taskId;

    @NonNull
    private String taskText;

    private LocalDateTime dueDate;

    @NonNull
    private LocalDateTime creationDate;

    @NonNull
    private Boolean isComplete;

    @NonNull
    private Long userId;

}

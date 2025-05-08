package TGJavaProjects.TasksApplication.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    private Long taskId;

    @NonNull
    private String taskText;

    private LocalDateTime dueDate;

    @NonNull
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @NonNull
    @Builder.Default
    private Boolean isComplete = false;

    @NonNull
    private Long userId;

    @NonNull
    @Builder.Default
    private Boolean isDeleted = false;

}

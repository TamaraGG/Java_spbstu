package TGJavaProjects.TasksApplication.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreatedEvent {
    private Long taskId;
    private Long userId;
    private String taskText;
}

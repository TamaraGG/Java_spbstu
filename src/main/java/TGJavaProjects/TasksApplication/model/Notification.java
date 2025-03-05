package TGJavaProjects.TasksApplication.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Builder
@Data
public class Notification {
    @NonNull
    private String text;

    @NonNull
    private long userId;

    private LocalDateTime date;

    private long TaskId;

}

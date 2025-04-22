package TGJavaProjects.TasksApplication.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class Notification {

    @NonNull
    private Long notificationId;

    @NonNull
    private String text;

    @NonNull
    private LocalDateTime date;

    @NonNull
    private Long TaskId;
}

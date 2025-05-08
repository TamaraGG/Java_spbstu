package TGJavaProjects.TasksApplication.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @NonNull
    private Long notificationId;

    @NonNull
    private String text;

    @NonNull
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    @NonNull
    private Long taskId;

    @Builder.Default
    private Boolean isRead = false;
}

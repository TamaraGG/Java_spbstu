package TGJavaProjects.TasksApplication.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long userId;

    private String firstName;
    private String lastName;

    @NonNull
    private String email;

    @NonNull
    @Builder.Default
    private LocalDateTime registrationDate = LocalDateTime.now();

}

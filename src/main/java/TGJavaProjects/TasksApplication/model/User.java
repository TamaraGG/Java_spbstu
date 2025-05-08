package TGJavaProjects.TasksApplication.model;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @NonNull
    private Long userId;

    private String firstName;
    private String lastName;
    private String email;

}

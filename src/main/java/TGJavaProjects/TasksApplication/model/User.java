package TGJavaProjects.TasksApplication.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
@AllArgsConstructor
public class User {

    @NonNull
    private Long userId;

    private String firstName;
    private String lastName;
    private String email;

}

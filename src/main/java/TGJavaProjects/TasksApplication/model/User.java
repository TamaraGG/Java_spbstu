package TGJavaProjects.TasksApplication.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class User {

    @NonNull
    private long userId;

    private String firstName;
    private String lastName;
    private String email;

}


package TGJavaProjects.TasksApplication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @NonNull
    @Column(nullable = false, length = 1000)
    private String taskText;

    private LocalDateTime dueDate;

    @NonNull
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @NonNull
    @Builder.Default
    @Column(nullable = false)
    private Boolean isComplete = false;

    @NonNull
    @Column(name = "user_id_ref", nullable = false)
    private Long userId;

    @NonNull
    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_ref", referencedColumnName = "userId",
            insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference("user-tasks")
    private User user;


    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonManagedReference("task-notifications")
    private List<Notification> notifications = new ArrayList<>();
}

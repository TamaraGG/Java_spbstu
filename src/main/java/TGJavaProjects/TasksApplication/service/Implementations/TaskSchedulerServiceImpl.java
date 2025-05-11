package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.service.TaskSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerServiceImpl implements TaskSchedulerService {


    private final TaskRepository taskRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional(readOnly = true)
    public void checkForOverdueTasks() {
        log.info("Scheduler: Checking for overdue tasks at {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        List<Task> activeTasks = taskRepository.findByIsDeletedFalseAndIsCompleteFalse();

        int overdueCount = 0;
        for (Task task : activeTasks) {
            if (task.getDueDate() != null && task.getDueDate().isBefore(now)) {
                log.warn("OVERDUE TASK FOUND: TaskId={}, UserId={}, DueDate={}, TaskText='{}'",
                        task.getTaskId(), task.getUserId(), task.getDueDate(), task.getTaskText());
                overdueCount++;
            }
        }
        if (overdueCount == 0) {
            log.info("Scheduler: No overdue tasks found.");
        } else {
            log.info("Scheduler: Found {} overdue tasks.", overdueCount);
        }
    }
}

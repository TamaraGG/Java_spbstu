package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import TGJavaProjects.TasksApplication.repository.TaskRepository;
import TGJavaProjects.TasksApplication.service.TaskMaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskMaintenanceServiceImpl implements TaskMaintenanceService {


    private final TaskRepository taskRepository;

    // Пример: проверять каждые 5 минут. cron = "0 */5 * * * ?"
    // Для демонстрации можно поставить чаще, например, каждые 30 секунд: fixedRate = 30000
    @Scheduled(fixedRate = 300000) // 300000 мс = 5 минут
    @Transactional(readOnly = true) // Транзакция только для чтения, если мы только читаем
    public void checkForOverdueTasks() {
        log.info("Scheduler: Checking for overdue tasks at {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        // Находим все задачи, которые не удалены и не выполнены
        List<Task> activeTasks = taskRepository.findByIsDeletedFalseAndIsCompleteFalse(); // Нужен такой метод в репозитории

        int overdueCount = 0;
        for (Task task : activeTasks) {
            if (task.getDueDate() != null && task.getDueDate().isBefore(now)) {
                log.warn("OVERDUE TASK FOUND: TaskId={}, UserId={}, DueDate={}, TaskText='{}'",
                        task.getTaskId(), task.getUserId(), task.getDueDate(), task.getTaskText());
                overdueCount++;
                // Здесь могла бы быть логика по отправке уведомлений о просрочке,
                // изменению статуса задачи и т.д.
            }
        }
        if (overdueCount == 0) {
            log.info("Scheduler: No overdue tasks found.");
        } else {
            log.info("Scheduler: Found {} overdue tasks.", overdueCount);
        }
    }
}

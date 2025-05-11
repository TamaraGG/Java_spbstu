package TGJavaProjects.TasksApplication.service.Implementations;

import TGJavaProjects.TasksApplication.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AnalyticsService {

    @Async // Помечаем метод как асинхронный
    public CompletableFuture<Void> recordTaskCompletionEvent(Task completedTask) {
        log.info("ASYNC: Starting to record task completion event for taskId: {}", completedTask.getTaskId());
        try {
            // Имитация долгой операции
            TimeUnit.SECONDS.sleep(5);
            log.info("ASYNC: Task completion event recorded for taskId: {}, userId: {}, text: '{}'",
                    completedTask.getTaskId(), completedTask.getUserId(), completedTask.getTaskText());
        } catch (InterruptedException e) {
            log.error("ASYNC: Recording task completion was interrupted for taskId: {}", completedTask.getTaskId(), e);
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture(null); // Возвращаем CompletableFuture для асинхронных методов
    }

    // Можно добавить еще один асинхронный метод, если нужно
    @Async
    public void performSomeOtherBackgroundTask(String data) {
        log.info("ASYNC: Starting some other background task with data: {}", data);
        try {
            TimeUnit.SECONDS.sleep(3);
            log.info("ASYNC: Finished some other background task with data: {}", data);
        } catch (InterruptedException e) {
            log.error("ASYNC: Background task was interrupted for data: {}", data, e);
            Thread.currentThread().interrupt();
        }
    }
}

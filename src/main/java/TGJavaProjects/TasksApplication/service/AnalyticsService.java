package TGJavaProjects.TasksApplication.service;

import TGJavaProjects.TasksApplication.model.Task;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface AnalyticsService {
    public CompletableFuture<Void> recordTaskCompletionEvent(Task completedTask);
    public void performSomeOtherBackgroundTask(String data);
}

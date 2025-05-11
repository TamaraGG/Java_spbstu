package TGJavaProjects.TasksApplication.service;

import org.springframework.stereotype.Service;

@Service
public interface TaskSchedulerService {
    void checkForOverdueTasks();
}
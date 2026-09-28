package br.edu.com.ifsul.taskorganizer.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.com.ifsul.taskorganizer.data.TaskDao;
import br.edu.com.ifsul.taskorganizer.data.TaskDatabase;
import br.edu.com.ifsul.taskorganizer.model.Task;

public class TaskRepository {

    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private final ExecutorService executorService;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<Task> getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }
}

package br.edu.com.ifsul.taskorganizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.edu.com.ifsul.taskorganizer.model.Task;
import br.edu.com.ifsul.taskorganizer.notification.NotificationHelper;
import br.edu.com.ifsul.taskorganizer.ui.AddEditTaskActivity;
import br.edu.com.ifsul.taskorganizer.ui.TaskAdapter;
import br.edu.com.ifsul.taskorganizer.viewmodel.TaskViewModel;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.createNotificationChannel(this);
        requestNotificationPermission();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this);
        recyclerView.setAdapter(taskAdapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, tasks -> {
            taskAdapter.setTasks(tasks);
            if (tasks == null || tasks.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
            }
        });

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });
    }

    private void requestNotificationPermission() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Toast.makeText(this, getString(R.string.enable_notifications_prompt), Toast.LENGTH_LONG).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.notification_permission_granted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.notifications_disabled_warning), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskDeleteClick(Task task) {
        NotificationHelper.cancelNotification(this, task.getId());
        taskViewModel.delete(task);
        Toast.makeText(this, getString(R.string.task_deleted), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskFinishedChange(Task task, boolean isFinished) {
        task.setFinished(isFinished);
        taskViewModel.update(task);
        if (isFinished) {
            NotificationHelper.cancelNotification(this, task.getId());
        }
    }
}

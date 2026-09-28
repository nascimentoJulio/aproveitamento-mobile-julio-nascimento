package br.edu.com.ifsul.taskorganizer.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.edu.com.ifsul.taskorganizer.R;
import br.edu.com.ifsul.taskorganizer.model.Category;
import br.edu.com.ifsul.taskorganizer.model.Priority;
import br.edu.com.ifsul.taskorganizer.model.Task;
import br.edu.com.ifsul.taskorganizer.notification.NotificationHelper;
import br.edu.com.ifsul.taskorganizer.viewmodel.TaskViewModel;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";

    private TextInputLayout tilTitle;
    private TextInputLayout tilDescription;
    private EditText etTitle;
    private EditText etDescription;
    private Button btnSelectDateTime;
    private Spinner spinnerPriority;
    private Spinner spinnerCategory;
    private CheckBox cbRemind;
    private CheckBox cbFinished;
    private Button btnSave;

    private TaskViewModel taskViewModel;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private int currentTaskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        tilTitle = findViewById(R.id.tilTitle);
        tilDescription = findViewById(R.id.tilDescription);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSelectDateTime = findViewById(R.id.btnSelectDateTime);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        cbRemind = findViewById(R.id.cbRemind);
        cbFinished = findViewById(R.id.cbFinished);
        btnSave = findViewById(R.id.btnSave);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        setupSpinners();
        updateDateTimeButtonText();

        btnSelectDateTime.setOnClickListener(v -> showDateTimePicker());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            currentTaskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
            if (currentTaskId != -1) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Task");
                }
                loadTaskDetails(currentTaskId);
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Task");
            }
        }

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void setupSpinners() {
        List<String> priorityList = new ArrayList<>();
        priorityList.add("-- Select Priority --");
        for (Priority p : Priority.values()) {
            priorityList.add(p.getDisplayName());
        }

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                priorityList
        );
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        List<String> categoryList = new ArrayList<>();
        categoryList.add("-- Select Category --");
        for (Category c : Category.values()) {
            categoryList.add(c.getDisplayName());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryList
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void updateDateTimeButtonText() {
        btnSelectDateTime.setText(dateTimeFormat.format(calendar.getTime()));
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            AddEditTaskActivity.this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                updateDateTimeButtonText();
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadTaskDetails(int taskId) {
        taskViewModel.getTaskById(taskId).observe(this, task -> {
            if (task != null) {
                etTitle.setText(task.getTitle());
                etDescription.setText(task.getDescription());
                calendar.setTimeInMillis(task.getDueDate());
                updateDateTimeButtonText();

                if (task.getPriority() != null) {
                    spinnerPriority.setSelection(task.getPriority().ordinal() + 1);
                }
                if (task.getCategory() != null) {
                    spinnerCategory.setSelection(task.getCategory().ordinal() + 1);
                }

                cbRemind.setChecked(task.isRemind());
                cbFinished.setChecked(task.isFinished());
            }
        });
    }

    private void saveTask() {
        boolean isValid = true;

        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        if (TextUtils.isEmpty(title)) {
            tilTitle.setError("Title is required");
            isValid = false;
        } else if (title.length() < 3) {
            tilTitle.setError("Title must be at least 3 characters");
            isValid = false;
        } else if (title.length() > 50) {
            tilTitle.setError("Title cannot exceed 50 characters");
            isValid = false;
        } else {
            tilTitle.setError(null);
        }

        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        if (description.length() > 200) {
            tilDescription.setError("Description cannot exceed 200 characters");
            isValid = false;
        } else {
            tilDescription.setError(null);
        }

        long dueDate = calendar.getTimeInMillis();
        long now = System.currentTimeMillis();
        if (currentTaskId == -1 && dueDate < now ) {
            Toast.makeText(this, "Due date cannot be prior to current date and time", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        int priorityPos = spinnerPriority.getSelectedItemPosition();
        if (priorityPos <= 0) {
            Toast.makeText(this, "Please select a valid priority", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        int categoryPos = spinnerCategory.getSelectedItemPosition();
        if (categoryPos <= 0) {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        Priority priority = Priority.values()[priorityPos - 1];
        Category category = Category.values()[categoryPos - 1];
        boolean remind = cbRemind.isChecked();
        boolean finished = cbFinished.isChecked();

        if (remind) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Toast.makeText(this, "Notifications are disabled in system settings for this app!", Toast.LENGTH_LONG).show();
            }
        }

        if (currentTaskId == -1) {
            Task newTask = new Task(title, description, dueDate, priority, category, finished, remind);
            taskViewModel.insert(newTask);
            if (remind) {
                NotificationHelper.scheduleNotification(this, newTask);
            }
            Toast.makeText(this, "Task created", Toast.LENGTH_SHORT).show();
        } else {
            Task updatedTask = new Task(currentTaskId, title, description, dueDate, priority, category, finished, remind);
            taskViewModel.update(updatedTask);
            if (remind && !finished) {
                NotificationHelper.scheduleNotification(this, updatedTask);
            } else {
                NotificationHelper.cancelNotification(this, currentTaskId);
            }
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}

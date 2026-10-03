package br.edu.com.ifsul.taskorganizer.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import br.edu.com.ifsul.taskorganizer.saripaar.ValidationError;
import br.edu.com.ifsul.taskorganizer.saripaar.Validator;
import br.edu.com.ifsul.taskorganizer.saripaar.annotation.Length;
import br.edu.com.ifsul.taskorganizer.saripaar.annotation.NotEmpty;
import br.edu.com.ifsul.taskorganizer.saripaar.annotation.Select;
import br.edu.com.ifsul.taskorganizer.viewmodel.TaskViewModel;

public class AddEditTaskActivity extends AppCompatActivity implements Validator.ValidationListener {

    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";

    private TextInputLayout tilTitle;
    private TextInputLayout tilDescription;

    @NotEmpty(message = "Title is required")
    @Length(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private EditText etTitle;

    @Length(max = 200, message = "Description cannot exceed 200 characters")
    private EditText etDescription;

    private Button btnSelectDateTime;

    @Select(defaultSelection = 0, message = "Please select a valid priority")
    private Spinner spinnerPriority;

    @Select(defaultSelection = 0, message = "Please select a valid category")
    private Spinner spinnerCategory;

    private CheckBox cbRemind;
    private CheckBox cbFinished;
    private Button btnSave;

    private TaskViewModel taskViewModel;
    private Validator validator;

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

        validator = new Validator(this);
        validator.setValidationListener(this);

        setupSpinners();

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
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.MINUTE, 5);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Task");
            }
        }

        updateDateTimeButtonText();

        btnSelectDateTime.setOnClickListener(v -> showDateTimePicker());

        btnSave.setOnClickListener(v -> validator.validate());
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
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            AddEditTaskActivity.this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                Calendar nowCal = Calendar.getInstance();
                                if (year == nowCal.get(Calendar.YEAR) &&
                                    month == nowCal.get(Calendar.MONTH) &&
                                    dayOfMonth == nowCal.get(Calendar.DAY_OF_MONTH)) {

                                    if (calendar.getTimeInMillis() < nowCal.getTimeInMillis() - 30000) {
                                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                                        Toast.makeText(this, "Selected time has passed today. Set for tomorrow " + dateTimeFormat.format(calendar.getTime()), Toast.LENGTH_LONG).show();
                                    }
                                }

                                updateDateTimeButtonText();
                            },
                            startHour,
                            startMinute,
                            true
                    );
                    timePickerDialog.show();
                },
                startYear,
                startMonth,
                startDay
        );

        if (currentTaskId == -1) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }

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

    @Override
    public void onValidationSucceeded() {
        tilTitle.setError(null);
        tilDescription.setError(null);

        long dueDate = calendar.getTimeInMillis();
        long now = System.currentTimeMillis();

        if (currentTaskId == -1 && dueDate < now - 60000) {
            Toast.makeText(this, "Due date must be in the future. Please select a valid date and time.", Toast.LENGTH_LONG).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        Priority priority = Priority.values()[spinnerPriority.getSelectedItemPosition() - 1];
        Category category = Category.values()[spinnerCategory.getSelectedItemPosition() - 1];
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

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        tilTitle.setError(null);
        tilDescription.setError(null);

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage();

            if (view == etTitle) {
                tilTitle.setError(message);
            } else if (view == etDescription) {
                tilDescription.setError(message);
            } else if (view instanceof Spinner) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

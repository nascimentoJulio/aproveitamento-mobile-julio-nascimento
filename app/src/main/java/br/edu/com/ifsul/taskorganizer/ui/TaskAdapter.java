package br.edu.com.ifsul.taskorganizer.ui;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.edu.com.ifsul.taskorganizer.R;
import br.edu.com.ifsul.taskorganizer.model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskDeleteClick(Task task);
        void onTaskFinishedChange(Task task, boolean isFinished);
    }

    private List<Task> tasks = new ArrayList<>();
    private final OnTaskClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.bind(currentTask);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbFinished;
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvDueDate;
        private final TextView tvPriority;
        private final TextView tvCategory;
        private final TextView tvRemind;
        private final ImageButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbFinished = itemView.findViewById(R.id.cbFinished);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvRemind = itemView.findViewById(R.id.tvRemind);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Task task) {
            tvTitle.setText(task.getTitle());

            if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
                tvDescription.setText(task.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            tvDueDate.setText("Due: " + dateFormat.format(new Date(task.getDueDate())));

            if (task.getPriority() != null) {
                tvPriority.setText("Priority: " + task.getPriority().getDisplayName());
            }

            if (task.getCategory() != null) {
                tvCategory.setText("Category: " + task.getCategory().getDisplayName());
            }

            if (task.isRemind()) {
                tvRemind.setVisibility(View.VISIBLE);
                tvRemind.setText("⏰ Remind set");
            } else {
                tvRemind.setVisibility(View.GONE);
            }

            cbFinished.setOnCheckedChangeListener(null);
            cbFinished.setChecked(task.isFinished());

            if (task.isFinished()) {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            cbFinished.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskFinishedChange(task, isChecked);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskDeleteClick(task);
                }
            });
        }
    }
}

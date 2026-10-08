package br.edu.com.ifsul.taskorganizer.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.edu.com.ifsul.taskorganizer.MainActivity;
import br.edu.com.ifsul.taskorganizer.R;
import br.edu.com.ifsul.taskorganizer.model.Task;

public class NotificationHelper {

    public static final String CHANNEL_ID = "task_reminders_channel";
    public static final String CHANNEL_NAME = "Task Reminders";
    public static final String CHANNEL_DESC = "Notifications for task reminders";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);
            channel.enableLights(true);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build();
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            channel.setSound(soundUri, audioAttributes);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private static int generatePositiveNotificationId(Task task) {
        if (task.getId() > 0) {
            return task.getId();
        }
        long time = System.currentTimeMillis();
        int id = (int) (time & 0x7FFFFFFF);
        return id > 0 ? id : 1001;
    }

    @SuppressLint("MissingPermission")
    public static void showNotificationNow(Context context, Task task) {
        createNotificationChannel(context);

        Intent openIntent = new Intent(context, MainActivity.class);
        int notificationId = generatePositiveNotificationId(task);

        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                notificationId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = task.getTitle() != null && !task.getTitle().trim().isEmpty() ? task.getTitle() : context.getString(R.string.task_reminder_title);
        String description = task.getDescription() != null && !task.getDescription().trim().isEmpty() ? task.getDescription() : context.getString(R.string.task_reminder_default_text);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (Exception ignored) {
        }
    }

    public static void scheduleNotification(Context context, Task task) {
        if (!task.isRemind()) {
            return;
        }

        createNotificationChannel(context);

        long triggerTime = task.getDueDate();
        long now = System.currentTimeMillis();

        if (triggerTime <= now + 5000) {
            showNotificationNow(context, task);
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        int requestCode = generatePositiveNotificationId(task);

        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra("EXTRA_TASK_ID", requestCode);
        intent.putExtra("EXTRA_TASK_TITLE", task.getTitle());
        intent.putExtra("EXTRA_TASK_DESCRIPTION", task.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Toast.makeText(context, context.getString(R.string.reminder_scheduled_format, sdf.format(new Date(triggerTime))), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } catch (Exception ex) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    public static void cancelNotification(Context context, int taskId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}

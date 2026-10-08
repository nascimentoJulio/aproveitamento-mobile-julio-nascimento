package br.edu.com.ifsul.taskorganizer.notification;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import br.edu.com.ifsul.taskorganizer.MainActivity;
import br.edu.com.ifsul.taskorganizer.R;

public class TaskNotificationReceiver extends BroadcastReceiver {

    @Override
    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("EXTRA_TASK_ID", -1);
        String title = intent.getStringExtra("EXTRA_TASK_TITLE");
        String description = intent.getStringExtra("EXTRA_TASK_DESCRIPTION");

        Intent openIntent = new Intent(context, MainActivity.class);
        int notificationId = taskId > 0 ? taskId : Math.abs((int) System.currentTimeMillis());

        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                notificationId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String defaultTitle = context.getString(R.string.task_reminder_title);
        String defaultText = context.getString(R.string.task_reminder_default_text);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title != null && !title.isEmpty() ? title : defaultTitle)
                .setContentText(description != null && !description.isEmpty() ? description : defaultText)
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
            // Ignored if permission was revoked or system error
        }
    }
}

package com.example.dailytaskmanager.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dailytaskmanager.DailyTaskApplication
import com.example.dailytaskmanager.MainActivity
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.model.Task
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TaskNotifier(private val context: Context) {

    @SuppressLint("MissingPermission") // se comprueba con areNotificationsEnabled()
    fun show(task: Task, silent: Boolean) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val remaining = Duration.between(LocalTime.now(), task.endingTime)
        if (remaining.isNegative || remaining.isZero) return

        val channel = if (silent) DailyTaskApplication.CHANNEL_ONGOING_ID
        else DailyTaskApplication.CHANNEL_ID
        val endText = task.endingTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.starter_icon)
            .setContentTitle(task.name)
            .setContentText(context.getString(R.string.notification_until, endText))
            .setOngoing(true)
            .setAutoCancel(false)
            .setTimeoutAfter(remaining.toMillis())
            .setContentIntent(openAppIntent(task.id))
            .setDeleteIntent(actionIntent(task.id, ACTION_DISMISSED))
            .addAction(0, context.getString(R.string.action_complete), actionIntent(task.id, ACTION_COMPLETE))
            .addAction(0, context.getString(R.string.action_snooze), actionIntent(task.id, ACTION_SNOOZE))
            .build()

        manager.notify(task.id.toInt(), notification)
    }

    fun cancel(taskId: Long) =
        NotificationManagerCompat.from(context).cancel(taskId.toInt())

    private fun openAppIntent(taskId: Long): PendingIntent =
        PendingIntent.getActivity(
            context,
            taskId.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun actionIntent(taskId: Long, action: String): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            (taskId.toString() + action).hashCode(),
            Intent(context, NotificationActionReceiver::class.java).apply {
                this.action = action
                putExtra(EXTRA_TASK_ID, taskId)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
}
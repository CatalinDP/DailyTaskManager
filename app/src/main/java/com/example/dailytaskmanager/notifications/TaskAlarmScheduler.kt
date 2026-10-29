package com.example.dailytaskmanager.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import timber.log.Timber
import androidx.core.content.getSystemService
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.utils.canScheduleExactAlarms
import com.example.dailytaskmanager.utils.nextTriggerTime
import java.time.ZoneId

class TaskAlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService<AlarmManager>()!!

    fun schedule(task: Task) {
        val trigger = nextTriggerTime(task.repetitionDays, task.startingTime) ?: return
        val millis = trigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        Timber.d("Scheduling task ${task.id} at $millis")
        setAlarm(millis, pendingIntent(task.id))
    }

    fun cancel(taskId: Long) {
        alarmManager.cancel(pendingIntent(taskId))
        alarmManager.cancel(snoozeIntent(taskId))
    }

    fun scheduleSnooze(taskId: Long, minutes: Long) {
        val millis = System.currentTimeMillis() + minutes * 60_000
        setAlarm(millis, snoozeIntent(taskId))
    }

    @SuppressLint("ScheduleExactAlarm") // Guarded by canScheduleExactAlarms()
    private fun setAlarm(millis: Long, pi: PendingIntent) {
        try {
            if (context.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pi)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pi)
            }
        } catch (e: SecurityException) {
            Timber.w(e, "Exact alarm denied, falling back to inexact")
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pi)
        }
    }

    private fun snoozeIntent(taskId: Long): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            (taskId.toString() + ACTION_SNOOZE_FIRE).hashCode(),
            Intent(context, NotificationActionReceiver::class.java).apply {
                action = ACTION_SNOOZE_FIRE
                putExtra(EXTRA_TASK_ID, taskId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun pendingIntent(taskId: Long): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            Intent(context, TaskAlarmReceiver::class.java).putExtra(EXTRA_TASK_ID, taskId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}

package com.example.dailytaskmanager.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import com.example.dailytaskmanager.model.Task
import java.time.ZoneId

class TaskAlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService<AlarmManager>()!!

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun schedule(task: Task) {
        val trigger = nextTriggerTime(task.repetitionDays, task.startingTime ?: return) ?: return
        val millis = trigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pi = pendingIntent(task.id)
        Log.d("NotifTest", "Scheduling ${task.name} - Millis: $millis - TaskId: ${task.id}")
        val canBeExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()
        try {
            if (canBeExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    millis,
                    pi
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    millis,
                    pi
                )
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pi)
        }
    }

    fun cancel(taskId: Long) = alarmManager.cancel(pendingIntent(taskId))

    private fun pendingIntent(taskId: Long): PendingIntent {
        Log.d("NotifTest", "TaskId: $taskId")
        return PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            Intent(context, TaskAlarmReceiver::class.java).putExtra("taskId", taskId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
package com.example.dailytaskmanager.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dailytaskmanager.DailyTaskApplication.Companion.CHANNEL_ID
import com.example.dailytaskmanager.MainActivity
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.dao.TaskDao
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class TaskAlarmReceiver : BroadcastReceiver(), KoinComponent {
    private val scheduler: TaskAlarmScheduler by inject()
    private val repository: TaskRepository by inject()
    private val notifier: TaskNotifier by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent?.getLongExtra("taskId", -1L)
        Log.d("NotifTest", "onReceive llamado, taskId=${intent?.getLongExtra("taskId", -1)}")
        if (taskId == -1L || context == null) return

        if (taskId == -2L) {
            sendTestNotification(context)
            return
        }

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val task = repository.getTaskById(taskId!!) ?: return@launch
                val doneToday = repository.isCompleted(taskId, LocalDate.now())
                if (!doneToday) notifier.show(task, silent = false)
                scheduler.schedule(task)
            } catch(e: SecurityException) {
                Log.e("NotifTest", "Error en receiver", e)
            } finally {
                pending.finish()
            }
        }
    }
}
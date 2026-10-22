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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TaskAlarmReceiver : BroadcastReceiver(), KoinComponent {
    private val scheduler: TaskAlarmScheduler by inject()
    private val taskDao: TaskDao by inject()

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
                val task = taskDao.getTaskById(taskId) ?: return@launch
                Log.d("NotifTest", "tarea encontrada: $task")
                showNotification(context, task)
                scheduler.schedule(task)
            } catch(e: SecurityException) {
                Log.e("NotifTest", "Error en receiver", e)
            } finally {
                pending.finish()
            }
        }
    }


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(context: Context?, task: Task) {
        if (context == null) return
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) {
            Log.w("NotifTest", "Notificaciones desactivadas")
            return
        }

        val openApp = PendingIntent.getActivity(
            context, task.id.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.starter_icon)
            .setContentTitle(task.name)
            .setContentText(context.getString(R.string.notification_task_text))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < 33
        ) {
            try {
                NotificationManagerCompat.from(context).notify(task.id.toInt(), notification)
            } catch (e: SecurityException) {
                Log.e("NotifTest", "Sin permiso de notificaciones", e)
            }
        }
    }
}
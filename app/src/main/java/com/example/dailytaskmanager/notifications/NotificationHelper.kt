package com.example.dailytaskmanager.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.dailytaskmanager.DailyTaskApplication
import com.example.dailytaskmanager.MainActivity
import com.example.dailytaskmanager.R

@Composable
fun rememberNotificationpermissionRequester(
    onResult: (granted: Boolean) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onResult)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> currentOnResult(granted) }

    return remember (context, launcher) {
        {
            val needsRequest = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

            if (needsRequest) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                currentOnResult(true)
            }
        }
    }
}

fun sendTestNotification(context: Context) {
    val manager = NotificationManagerCompat.from(context)

    if (!manager.areNotificationsEnabled()) {
        Log.e("NotifTest", "Notificaciones desactivadas o sin permiso POST_NOTIFICATIONS")
        return
    }

    val openApp = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, DailyTaskApplication.CHANNEL_ID)
        .setSmallIcon(R.drawable.starter_icon)
        .setContentTitle(context.getString(R.string.notification_test_title))
        .setContentText(context.getString(R.string.notification_test_text))
        .setContentIntent(openApp)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    try {
        manager.notify(9999, notification)
        Log.d("NotifTest", "notify() llamado")
    } catch (e: SecurityException) {
        Log.e("NotifTest", "Falta permiso POST_NOTIFICATIONS", e)
    }
}

fun scheduleTestAlarm(context: Context) {
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val pi = PendingIntent.getBroadcast(
        context,
        9998,
        Intent(context, TaskAlarmReceiver::class.java).putExtra("taskId", -2L),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val triggerAt = System.currentTimeMillis() + 10_000

    val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()
    Log.d("NotifTest", "Alarma en 10s. Exactas permitidas: $canExact")

    if (canExact) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    } else {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    }
}
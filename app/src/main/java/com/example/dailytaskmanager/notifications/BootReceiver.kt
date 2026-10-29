package com.example.dailytaskmanager.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber
import com.example.dailytaskmanager.notifications.standUp.StandUpScheduler
import com.example.dailytaskmanager.repository.SettingsRepository
import com.example.dailytaskmanager.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {
    // The exact-alarm action is an inlined constant; below API 31 it is simply never broadcast
    @SuppressLint("InlinedApi")
    private val handledActions = setOf(
        Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED,
        Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED,
        AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
    )
    private val repository: TaskRepository by inject()
    private val scheduler: TaskAlarmScheduler by inject()
    private val standUpScheduler: StandUpScheduler by inject()
    private val settings: SettingsRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action !in handledActions) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.getAllTasksList().forEach { task ->
                    scheduler.schedule(task)
                }
                standUpScheduler.reschedule(settings.standUpConfig.first())
                Timber.d("boot: recordatorios reprogramados")
            } catch (e: Exception) {
                Timber.e(e, "Reschedule failed")
            } finally {
                pending.finish()
            }
        }
    }
}

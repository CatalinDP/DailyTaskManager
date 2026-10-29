package com.example.dailytaskmanager.notifications.standUp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber
import com.example.dailytaskmanager.notifications.TaskNotifier
import com.example.dailytaskmanager.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalTime

class StandUpReceiver : BroadcastReceiver(), KoinComponent {
    private val settings: SettingsRepository by inject()
    private val scheduler: StandUpScheduler by inject()
    private val notifier: TaskNotifier by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Timber.d("onReceive: disparada a las ${LocalTime.now()}")
                val config = settings.standUpConfig.first()
                Timber.d("onReceive: config leída = $config")
                if (!config.enabled) {
                    Timber.d("onReceive: desactivado, cadena cortada")
                    return@launch
                }
                notifier.showStandUp()
                Timber.d("onReceive: notificación mostrada, reprogramando")
                scheduler.reschedule(config)
            } catch (e: Exception) {
                Timber.e(e, "onReceive falló")
            } finally {
                pending.finish()
            }
        }
    }
}
package com.example.dailytaskmanager.notifications.standUp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import timber.log.Timber
import androidx.core.content.getSystemService
import com.example.dailytaskmanager.model.StandUpConfig
import java.time.ZoneId

const val STAND_UP_REQUEST_CODE = Int.MAX_VALUE

class StandUpScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService<AlarmManager>()!!

    fun reschedule(config: StandUpConfig) {
        val next = if (config.enabled)
            nextStandUpTime(config.interval.hours, config.start, config.end)
        else null

        if (next == null) {
            Timber.d("reschedule: CANCELADA (intervalo=${config.interval}, rango=${config.start}-${config.end})")
            cancel()
            return
        }
        val millis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        Timber.d("reschedule: PROGRAMADA para $next (en ${(millis - System.currentTimeMillis()) / 60_000} min)")
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent())
    }

    fun cancel() = alarmManager.cancel(pendingIntent())

    private fun pendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            STAND_UP_REQUEST_CODE,
            Intent(context, StandUpReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}
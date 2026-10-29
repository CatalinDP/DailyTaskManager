package com.example.dailytaskmanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import androidx.core.content.getSystemService
import com.example.dailytaskmanager.debug.DebugSeeder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import timber.log.Timber

class DailyTaskApplication : Application() {
    companion object {
        const val CHANNEL_ID = "task_alerts"
        const val CHANNEL_ONGOING_ID = "task_ongoing"
        const val CHANNEL_STAND_UP_ID = "stand_up"
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidContext(this@DailyTaskApplication)
            modules(appModule)
        }
        if (BuildConfig.DEBUG) {
            val handler = CoroutineExceptionHandler { _, e -> Timber.e(e, "Debug seeding failed") }
            CoroutineScope(Dispatchers.IO + handler).launch {
                DebugSeeder.seed(getKoin().get(), getKoin().get())
            }
        }
        createNotificationChannels()
    }
    private fun createNotificationChannels() {
        val manager = getSystemService<NotificationManager>()!!

        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.channel_alerts_name),
                    NotificationManager.IMPORTANCE_HIGH
                ),
                NotificationChannel(
                    CHANNEL_ONGOING_ID,
                    getString(R.string.channel_ongoing_name),
                    NotificationManager.IMPORTANCE_LOW
                ),
                NotificationChannel(
                    CHANNEL_STAND_UP_ID,
                    getString(R.string.channel_standup_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        )
    }
}
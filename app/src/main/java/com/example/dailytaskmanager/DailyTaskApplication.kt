package com.example.dailytaskmanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import androidx.core.content.getSystemService
import com.example.dailytaskmanager.BuildConfig
import com.example.dailytaskmanager.viewmodel.DebugSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin

class DailyTaskApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DailyTaskApplication)
            modules(appModule)
        }
        if (BuildConfig.DEBUG) {
            CoroutineScope(Dispatchers.IO).launch {
                DebugSeeder.seed(getKoin().get())
            }
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService<NotificationManager>()!!.createNotificationChannel(channel)
        val channelOnGoing = NotificationChannel(
            CHANNEL_ONGOING_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService<NotificationManager>()!!.createNotificationChannel(channelOnGoing)
    }

    companion object {
        const val CHANNEL_ID = "task_reminders"
        const val CHANNEL_ONGOING_ID = "task_ongoing"
    }
}
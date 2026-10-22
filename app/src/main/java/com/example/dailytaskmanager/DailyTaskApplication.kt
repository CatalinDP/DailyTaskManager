package com.example.dailytaskmanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import androidx.core.content.getSystemService

class DailyTaskApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DailyTaskApplication)
            modules(appModule)
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
    }

    companion object {
        const val CHANNEL_ID = "task_reminders"
    }
}
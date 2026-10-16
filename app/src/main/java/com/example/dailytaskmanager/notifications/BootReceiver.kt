package com.example.dailytaskmanager.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.dailytaskmanager.dao.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver: BroadcastReceiver(), KoinComponent {
    private val taskDao: TaskDao by inject()
    private val scheduler: TaskAlarmScheduler by inject()
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                taskDao.getAllTasksList().forEach { task ->
                    Log.d("NotifTest", "Tarea encontrada: $task.name")
                    scheduler.schedule(task)
                }
            } finally {
                pending.finish()
            }
        }
    }


}
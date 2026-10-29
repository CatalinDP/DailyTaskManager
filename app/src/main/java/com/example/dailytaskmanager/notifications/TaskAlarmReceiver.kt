package com.example.dailytaskmanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber
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
        val taskId = intent?.getLongExtra(EXTRA_TASK_ID, -1L) ?: -1L
        Timber.d("onReceive llamado, taskId=$taskId")
        if (taskId == -1L || context == null) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val task = repository.getTaskById(taskId) ?: return@launch
                val doneToday = repository.isCompleted(taskId, LocalDate.now())
                if (!doneToday) notifier.show(task, silent = false)
                scheduler.schedule(task)
            } catch (e: Exception) {
                Timber.e(e, "Task alarm handling failed")
            } finally {
                pending.finish()
            }
        }
    }
}

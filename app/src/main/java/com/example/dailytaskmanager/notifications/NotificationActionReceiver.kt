package com.example.dailytaskmanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dailytaskmanager.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime

class NotificationActionReceiver : BroadcastReceiver(), KoinComponent {
    private val repository: TaskRepository by inject()
    private val notifier: TaskNotifier by inject()
    private val scheduler: TaskAlarmScheduler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val action = intent.action
        if (taskId == -1L || action == null) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (action) {
                    ACTION_COMPLETE -> {
                        repository.setCompleted(taskId, LocalDate.now(), true)
                        notifier.cancel(taskId)
                    }
                    ACTION_SNOOZE -> {
                        notifier.cancel(taskId)
                        scheduler.scheduleSnooze(taskId, SNOOZE_MINUTES)
                    }
                    // A snoozed reminder must alert again, so it is not posted on the silent channel
                    ACTION_SNOOZE_FIRE -> {
                        val task = repository.getTaskById(taskId) ?: return@launch
                        if (!repository.isCompleted(taskId, LocalDate.now())) {
                            notifier.show(task, silent = false)
                        }
                    }
                    ACTION_DISMISSED, ACTION_SHOW -> {
                        val task = repository.getTaskById(taskId) ?: return@launch
                        val now = LocalTime.now()
                        val active = !repository.isCompleted(taskId, LocalDate.now()) &&
                                now >= task.startingTime && now < task.endingTime
                        if (active) notifier.show(task, silent = true)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Notification action $action failed")
            } finally {
                pending.finish()
            }
        }
    }
}

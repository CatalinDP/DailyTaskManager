package com.example.dailytaskmanager.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.dao.TaskDao
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.TaskCompletion
import com.example.dailytaskmanager.notifications.TaskAlarmScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.collect
import java.time.LocalDate

class TaskRepositoryImpl(
    private val dao: TaskDao,
    private val scheduler: TaskAlarmScheduler
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return dao.getAllTasks()
    }

    override fun getAllTasksList(): List<Task> {
        return dao.getAllTasksList()
    }

    override suspend fun getTaskById(id: Long): Task? {
        return dao.getTaskById(id)
    }

    @SuppressLint("ScheduleExactAlarm")
    override suspend fun insertTask(task: Task) {
        val id = dao.insertTask(task)
        scheduler.schedule(task.copy(id = id))
    }

    override suspend fun updateTask(task: Task) {
        dao.updateTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        dao.deleteTask(task)
        scheduler.cancel(task.id)
    }

    override suspend fun insertAll(tasks: List<Task>) {
        dao.insertAll(tasks)
    }

    override fun cancelReminder(task: Task) {
        scheduler.cancel(task.id)
    }

    @SuppressLint("ScheduleExactAlarm")
    override fun scheduleReminder(task: Task) {
        scheduler.schedule(task)
    }

    override fun completedIds(date: LocalDate): Flow<List<Long>> {
        return dao.completedIds(date)
    }

    override fun completionDates(
        from: LocalDate,
        to: LocalDate
    ): Flow<List<TaskCompletion>> {
        return dao.completionDates(from, to)
    }

    override fun allCompletions(): Flow<List<TaskCompletion>> {
        return dao.allCompletions()
    }

    override suspend fun isCompleted(taskId: Long, date: LocalDate): Boolean {
        return dao.isCompleted(taskId, date)
    }

    override suspend fun setCompleted(taskId: Long, date: LocalDate, done: Boolean) {
        Log.d("COMPLETION", "setCompleted: $taskId, $date, $done")
        if (done) dao.markDone(TaskCompletion(taskId, date))
        else dao.unmarkDone(taskId, date)
    }

}
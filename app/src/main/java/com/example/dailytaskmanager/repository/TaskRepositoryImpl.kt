package com.example.dailytaskmanager.repository

import android.annotation.SuppressLint
import com.example.dailytaskmanager.dao.TaskDao
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.notifications.TaskAlarmScheduler
import kotlinx.coroutines.flow.Flow

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

    override suspend fun getTaskById(id: Int): Task? {
        return dao.getTaskById(id)
    }

    @SuppressLint("ScheduleExactAlarm")
    override suspend fun insertTask(task: Task) {
        dao.insertTask(task)
        scheduler.schedule(task)
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

}
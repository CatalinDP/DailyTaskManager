package com.example.dailytaskmanager.repository

import timber.log.Timber
import com.example.dailytaskmanager.dao.TaskDao
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.TaskCompletion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskRepositoryImpl(
    private val dao: TaskDao,
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return dao.getAllTasks()
    }

    override suspend fun getAllTasksList(): List<Task> {
        return dao.getAllTasksList()
    }

    override suspend fun getTaskById(id: Long): Task? {
        return dao.getTaskById(id)
    }

    override suspend fun insertTask(task: Task): Long {
        return dao.insertTask(task)
    }

    override suspend fun updateTask(task: Task) {
        dao.updateTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        dao.deleteTask(task)
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

    override suspend fun isCompleted(taskId: Long, date: LocalDate): Boolean {
        return dao.isCompleted(taskId, date)
    }

    override suspend fun setCompleted(taskId: Long, date: LocalDate, done: Boolean) {
        Timber.d("setCompleted: $taskId, $date, $done")
        if (done) dao.markDone(TaskCompletion(taskId, date))
        else dao.unmarkDone(taskId, date)
    }

}

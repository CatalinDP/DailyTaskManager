package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getAllTasksList(): List<Task>
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun insertAll(tasks: List<Task>)

    fun cancelReminder(task: Task)
    fun scheduleReminder(task: Task)
}

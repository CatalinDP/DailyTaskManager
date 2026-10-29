package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.TaskCompletion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getAllTasksList(): List<Task>
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun setCompleted(taskId: Long, date: LocalDate, done: Boolean)

    fun completedIds(date: LocalDate): Flow<List<Long>>
    fun completionDates(from: LocalDate, to: LocalDate): Flow<List<TaskCompletion>>
    suspend fun isCompleted(taskId: Long, date: LocalDate): Boolean
}

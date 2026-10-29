package com.example.dailytaskmanager.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.TaskCompletion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY startingTime ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY startingTime ASC")
    suspend fun getAllTasksList(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markDone(completion: TaskCompletion)

    @Query("DELETE FROM task_completion WHERE taskId = :taskId AND date = :date")
    suspend fun unmarkDone(taskId: Long, date: LocalDate)

    @Query("SELECT taskId FROM task_completion WHERE date = :date")
    fun completedIds(date: LocalDate): Flow<List<Long>>

    @Query("SELECT * FROM task_completion WHERE date BETWEEN :from AND :to")
    fun completionDates(from: LocalDate, to: LocalDate): Flow<List<TaskCompletion>>

    @Query("SELECT EXISTS(SELECT 1 FROM task_completion WHERE taskId = :taskId AND date = :date)")
    suspend fun isCompleted(taskId: Long, date: LocalDate): Boolean
}
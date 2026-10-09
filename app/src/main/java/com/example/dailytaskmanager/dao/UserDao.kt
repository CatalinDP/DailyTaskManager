package com.example.dailytaskmanager.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

@Dao
interface UserDao {
    @Query("SELECT name FROM users")
    suspend fun getUserName(): String?

    @Query("SELECT * FROM users")
    suspend fun getUser(): User?

    @Query("SELECT startingTime FROM users")
    suspend fun getUserStartingTime(): LocalTime?

    @Insert
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}
package com.example.dailytaskmanager.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.dailytaskmanager.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun observeUser(): Flow<User?>

    @Upsert
    suspend fun upsertUser(user: User)

    @Query("UPDATE users SET name = :name")
    suspend fun updateUserName(name: String)

    @Query("UPDATE users SET startingTime = :time")
    suspend fun updateStartingTime(time: LocalTime?)
}

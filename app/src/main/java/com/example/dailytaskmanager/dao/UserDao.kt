package com.example.dailytaskmanager.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.dailytaskmanager.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun observeUser(): Flow<User?>

    @Upsert
    suspend fun upsertUser(user: User)
}

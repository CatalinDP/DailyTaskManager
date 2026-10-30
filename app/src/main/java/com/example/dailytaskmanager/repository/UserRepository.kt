package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface UserRepository {
    val user: Flow<User?>

    suspend fun upsertUser(user: User)

    suspend fun setUserName(name: String)
    suspend fun setStartingTime(time: LocalTime)
}

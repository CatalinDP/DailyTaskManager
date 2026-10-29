package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val user: Flow<User?>

    suspend fun upsertUser(user: User)
}

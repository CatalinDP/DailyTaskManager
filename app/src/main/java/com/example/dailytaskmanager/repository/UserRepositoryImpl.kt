package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.dao.UserDao
import com.example.dailytaskmanager.model.User
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val userDao: UserDao
): UserRepository {
    override val user: Flow<User?> = userDao.observeUser()

    override suspend fun upsertUser(user: User) {
        userDao.upsertUser(user)
    }
}

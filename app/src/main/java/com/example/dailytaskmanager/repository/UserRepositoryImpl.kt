package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.dao.UserDao
import com.example.dailytaskmanager.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import java.time.LocalTime

class UserRepositoryImpl(
    private val userDao: UserDao
): UserRepository {
    override val user: Flow<User?> = userDao.observeUser()

    override suspend fun upsertUser(user: User) {
        userDao.upsertUser(user)
    }

    override suspend fun setUserName(name: String) {
        userDao.updateUserName(name)
    }

    override suspend fun setStartingTime(time: LocalTime) {
        userDao.updateStartingTime(time)
    }
}

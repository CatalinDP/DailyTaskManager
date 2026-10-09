package com.example.dailytaskmanager

import com.example.dailytaskmanager.database.AppDatabase
import com.example.dailytaskmanager.notifications.TaskAlarmScheduler
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.repository.TaskRepositoryImpl
import com.example.dailytaskmanager.viewmodel.CreateTaskViewModel
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import com.example.dailytaskmanager.viewmodel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }

    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().userDao() }

    single { TaskAlarmScheduler(androidContext()) }

    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single { get<TaskRepository>() as TaskRepositoryImpl }

    viewModel { TaskViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { CreateTaskViewModel(get()) }

}
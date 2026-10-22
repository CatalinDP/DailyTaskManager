package com.example.dailytaskmanager

import com.example.dailytaskmanager.database.AppDatabase
import com.example.dailytaskmanager.notifications.TaskAlarmScheduler
import com.example.dailytaskmanager.repository.SettingsRepository
import com.example.dailytaskmanager.repository.SettingsRepositoryImpl
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.repository.TaskRepositoryImpl
import com.example.dailytaskmanager.viewmodel.CreateTaskViewModel
import com.example.dailytaskmanager.viewmodel.HomeViewModel
import com.example.dailytaskmanager.viewmodel.SettingsViewModel
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import com.example.dailytaskmanager.viewmodel.UserViewModel
import com.example.dailytaskmanager.weather.WeatherRepository
import com.example.dailytaskmanager.weather.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    singleOf(::WeatherRepository)

    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().userDao() }

    single { TaskAlarmScheduler(androidContext()) }

    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single { get<TaskRepository>() as TaskRepositoryImpl }
    single { SettingsRepositoryImpl(androidContext()) }

    viewModel { SettingsViewModel(get()) }
    viewModel { TaskViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { CreateTaskViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
}
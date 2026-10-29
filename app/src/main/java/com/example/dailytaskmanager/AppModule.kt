package com.example.dailytaskmanager

import com.example.dailytaskmanager.database.AppDatabase
import com.example.dailytaskmanager.notifications.TaskAlarmScheduler
import com.example.dailytaskmanager.notifications.TaskNotifier
import com.example.dailytaskmanager.notifications.standUp.StandUpScheduler
import com.example.dailytaskmanager.repository.LocationRepository
import com.example.dailytaskmanager.repository.LocationRepositoryImpl
import com.example.dailytaskmanager.repository.SettingsRepository
import com.example.dailytaskmanager.repository.SettingsRepositoryImpl
import com.example.dailytaskmanager.repository.TaskReminderUseCase
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.repository.TaskRepositoryImpl
import com.example.dailytaskmanager.repository.UserRepository
import com.example.dailytaskmanager.repository.UserRepositoryImpl
import com.example.dailytaskmanager.repository.WeatherRepository
import com.example.dailytaskmanager.viewmodel.CreateTaskViewModel
import com.example.dailytaskmanager.viewmodel.HomeViewModel
import com.example.dailytaskmanager.viewmodel.MainViewModel
import com.example.dailytaskmanager.viewmodel.SettingsViewModel
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import com.example.dailytaskmanager.viewmodel.UserViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
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
            expectSuccess = true
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 15_000
            }
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

    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }
    single { TaskNotifier(androidContext()) }
    single { StandUpScheduler(androidContext()) }
    single { TaskReminderUseCase(get(), get()) }
    single<LocationRepository> { LocationRepositoryImpl(androidContext()) }

    viewModel { SettingsViewModel(get(), get()) }
    viewModel { TaskViewModel(get(), get()) }
    viewModel { UserViewModel(get()) }
    viewModel { CreateTaskViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
}
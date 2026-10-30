package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.model.StandUpConfig
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<ThemeMode>
    val unit: Flow<TempUnit>
    val showWeather: Flow<Boolean>
    val standUpConfig: Flow<StandUpConfig>
    val dismissedHints: Flow<Set<String>>
    suspend fun setTheme(value: ThemeMode)
    suspend fun setUnit(value: TempUnit)
    suspend fun showWeatherOnHome(value: Boolean)
    suspend fun setStandUpConfig(config: StandUpConfig)

    suspend fun dismissHint(id: String)
    suspend fun resetHints()
}
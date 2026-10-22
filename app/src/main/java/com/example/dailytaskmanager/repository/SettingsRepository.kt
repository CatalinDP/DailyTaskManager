package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.model.LeadTime
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import java.time.LocalTime

interface SettingsRepository {
    suspend fun setLeadTime(time: LeadTime)
    suspend fun setTheme(value: ThemeMode)
    suspend fun setUnit(value: TempUnit)
    suspend fun setUsername(value: String)
    suspend fun setStartingTime(value: LocalTime)
    suspend fun showWeatherOnHome(value: Boolean)
}
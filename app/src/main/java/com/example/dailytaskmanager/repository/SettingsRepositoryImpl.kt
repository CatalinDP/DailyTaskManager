package com.example.dailytaskmanager.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dailytaskmanager.model.LeadTime
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepositoryImpl(private val context: Context): SettingsRepository {
    override suspend fun setLeadTime(time: LeadTime) {
        TODO("Not yet implemented")
    }

    private object Keys{
        val THEME = stringPreferencesKey("theme")
        val UNIT = stringPreferencesKey("unit")
        val WEATHER = booleanPreferencesKey("showWeather")
    }

    val theme: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }
    val unit: Flow<TempUnit> = context.dataStore.data.map { prefs ->
        prefs[Keys.UNIT]?.let { runCatching { TempUnit.valueOf(it) }.getOrNull() } ?: TempUnit.CELSIUS
    }
    val showWeather: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.WEATHER]?.let { runCatching { it }.getOrNull() } ?: false
    }

    override suspend fun setTheme(value: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME] = value.name }
    }

    override suspend fun setUnit(value: TempUnit) {
        context.dataStore.edit { it[Keys.UNIT] = value.name }
    }

    override suspend fun setUsername(value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setStartingTime(value: LocalTime) {
        TODO("Not yet implemented")
    }

    override suspend fun showWeatherOnHome(value: Boolean) {
       context.dataStore.edit { it[Keys.WEATHER] = value }
    }
}
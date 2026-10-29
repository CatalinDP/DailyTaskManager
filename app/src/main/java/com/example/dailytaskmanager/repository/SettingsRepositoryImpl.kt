package com.example.dailytaskmanager.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dailytaskmanager.model.StandUpConfig
import com.example.dailytaskmanager.model.StandUpInterval
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.time.LocalTime

private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepositoryImpl(private val context: Context): SettingsRepository {
    private object Keys{
        val THEME = stringPreferencesKey("theme")
        val UNIT = stringPreferencesKey("unit")
        val WEATHER = booleanPreferencesKey("showWeather")
        val STAND_INTERVAL = stringPreferencesKey("stand_up_interval")
        val STAND_START = intPreferencesKey("stand_up_start")
        val STAND_END = intPreferencesKey("stand_up_end")
    }

    // A read failure falls back to defaults instead of crashing every collector (splash included)
    private val prefs: Flow<Preferences> = context.dataStore.data.catch { e ->
        if (e is IOException) {
            Timber.e(e, "Reading settings failed, using defaults")
            emit(emptyPreferences())
        } else {
            throw e
        }
    }

    override val theme: Flow<ThemeMode> = prefs.map { prefs ->
        prefs[Keys.THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }
    override val unit: Flow<TempUnit> = prefs.map { prefs ->
        prefs[Keys.UNIT]?.let { runCatching { TempUnit.valueOf(it) }.getOrNull() } ?: TempUnit.CELSIUS
    }
    override val showWeather: Flow<Boolean> = prefs.map { prefs ->
        prefs[Keys.WEATHER] ?: false
    }

    override suspend fun setTheme(value: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME] = value.name }
    }

    override suspend fun setUnit(value: TempUnit) {
        context.dataStore.edit { it[Keys.UNIT] = value.name }
    }

    override val standUpConfig: Flow<StandUpConfig> = prefs.map { prefs ->
        val default = StandUpConfig()
        StandUpConfig(
            interval = prefs[Keys.STAND_INTERVAL]
                ?.let { runCatching { StandUpInterval.valueOf(it) }.getOrNull() }
                ?: default.interval,
            start = prefs[Keys.STAND_START]?.let { LocalTime.of(it / 60, it % 60) } ?: default.start,
            end = prefs[Keys.STAND_END]?.let { LocalTime.of(it / 60, it % 60) } ?: default.end
        )
    }

    override suspend fun setStandUpConfig(config: StandUpConfig) {
        context.dataStore.edit {
            it[Keys.STAND_INTERVAL] = config.interval.name
            it[Keys.STAND_START] = config.start.hour * 60 + config.start.minute
            it[Keys.STAND_END] = config.end.hour * 60 + config.end.minute
        }
    }

    override suspend fun showWeatherOnHome(value: Boolean) {
       context.dataStore.edit { it[Keys.WEATHER] = value }
    }
}

package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import com.example.dailytaskmanager.repository.SettingsRepository
import com.example.dailytaskmanager.repository.SettingsRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepositoryImpl
): ViewModel() {
    val theme = repository.theme.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM
    )
    val unit = repository.unit.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        TempUnit.CELSIUS
    )
    val showWeather = repository.showWeather.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), false
    )

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }

    fun setUnit(unit: TempUnit) {
        viewModelScope.launch {
            repository.setUnit(unit)
        }
    }

    fun setShowWeather(value: Boolean) {
        viewModelScope.launch {
            repository.showWeatherOnHome(value)
        }
    }
}
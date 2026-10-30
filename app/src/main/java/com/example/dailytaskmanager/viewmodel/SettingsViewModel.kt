package com.example.dailytaskmanager.viewmodel

import timber.log.Timber
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.StandUpConfig
import com.example.dailytaskmanager.model.StandUpInterval
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import com.example.dailytaskmanager.notifications.standUp.StandUpScheduler
import com.example.dailytaskmanager.repository.SettingsRepository
import com.example.dailytaskmanager.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val userRepository: UserRepository,
    private val standUpScheduler: StandUpScheduler
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

    val user = userRepository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val dismissedHints =repository.dismissedHints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

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

    val standUp = repository.standUpConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StandUpConfig())

    private fun updateStandUp(change: (StandUpConfig) -> StandUpConfig) {
        viewModelScope.launch {
            val updated = change(repository.standUpConfig.first())
            Timber.d("ajustes: guardando $updated")
            repository.setStandUpConfig(updated)
            standUpScheduler.reschedule(updated)
        }
    }
    fun setStandUpInterval(value: StandUpInterval) = updateStandUp { it.copy(interval = value) }
    fun setStandUpWindow(start: LocalTime, end: LocalTime) = updateStandUp { it.copy(start = start, end = end) }

    fun dismissHint(id: String) {
        viewModelScope.launch {
            repository.dismissHint(id)
        }
    }

    fun resetHints() {
        viewModelScope.launch {
            repository.resetHints()
        }
    }

    fun changeUserName(name: String) {
        viewModelScope.launch {
            userRepository.setUserName(name)
        }
    }

    fun changeStartingTime(time: LocalTime) {
        viewModelScope.launch {
            userRepository.setStartingTime(time)
        }
    }
}
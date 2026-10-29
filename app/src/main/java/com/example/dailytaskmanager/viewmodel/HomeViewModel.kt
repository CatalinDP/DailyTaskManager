package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.ErrorReason
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.WeatherState
import com.example.dailytaskmanager.repository.LocationRepository
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.repository.WeatherRepository
import com.example.dailytaskmanager.utils.toDay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val taskRepository: TaskRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    private val _weather = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weather = _weather.asStateFlow()

    private val clock = flow { while (true) { emit(LocalDateTime.now()); delay(60_000.milliseconds) } }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    // Re-evaluated when the date changes, so the list stays correct past midnight
    private val todayTasksFlow = combine(
        taskRepository.getAllTasks(),
        clock.map { it.toLocalDate() }.distinctUntilChanged()
    ) { tasks, date ->
        val day = date.toDay()
        tasks.filter { day in it.repetitionDays }.sortedBy { it.startingTime }
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    val todayTasks: StateFlow<List<Task>> = todayTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val nextTasks: StateFlow<List<Task>> = combine(todayTasksFlow, clock) { tasks, now ->
        tasks.filter { it.endingTime >= now.toLocalTime() }.take(3)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Loads the weather once per ViewModel; already loaded data is kept when coming back to Home. */
    fun loadWeather() {
        if (_weather.value is WeatherState.Success) return
        viewModelScope.launch {
            _weather.value = WeatherState.Loading
            val location = locationRepository.currentLocation()
            if (location == null) {
                _weather.value = WeatherState.Error(ErrorReason.LOCATION_DENIED)
                return@launch
            }
            weatherRepository.get(location.latitude, location.longitude)
                .onSuccess { _weather.value = WeatherState.Success(it) }
                .onFailure { e ->
                    val reason = (e as? WeatherRepository.WeatherException)?.reason ?: ErrorReason.NETWORK
                    _weather.value = WeatherState.Error(reason)
                }
        }
    }

    fun onLocationDenied() {
        _weather.value = WeatherState.Error(ErrorReason.LOCATION_DENIED)
    }
}

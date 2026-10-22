package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.ErrorReason
import com.example.dailytaskmanager.model.WeatherState
import com.example.dailytaskmanager.utils.getCurrentDayName
import com.example.dailytaskmanager.weather.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val repository: WeatherRepository,
    private val taskViewModel: TaskViewModel
): ViewModel() {
    private val _weather = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weather = _weather.asStateFlow()
    var coordinates = MutableStateFlow(Pair(0.0, 0.0))
    private val ticker = flow { while (true)  { emit(LocalTime.now()); delay(180_000.milliseconds) } }

    private val remainingToday = combine(taskViewModel.selectedDayTasks, ticker) { tasks, now ->
        tasks.filter { getCurrentDayName() in it.repetitionDays && it.endingTime!! >= now }
            .sortedBy { it.startingTime }
    }

    val remainingCount = remainingToday.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val nextTasks = remainingToday.map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weather.value = WeatherState.Loading
            repository.get(lat, lon)
                .onSuccess { _weather.value = WeatherState.Success(it) }
                .onFailure { _weather.value = WeatherState.Error(ErrorReason.NETWORK) }
        }
    }

    fun onLocationDenied() {
        _weather.value = WeatherState.Error(ErrorReason.LOCATION_DENIED)
    }
}
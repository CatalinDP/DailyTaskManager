package com.example.dailytaskmanager.model

import com.example.dailytaskmanager.weather.WeatherResponse

sealed interface WeatherState {
    data object Loading: WeatherState
    data class Success(val data: WeatherResponse): WeatherState
    data class Error(val reason: ErrorReason) : WeatherState
}

enum class ErrorReason { NETWORK, LOCATION_DENIED }
package com.example.dailytaskmanager.model

sealed interface WeatherState {
    data object Loading: WeatherState
    data class Success(val data: WeatherResponse): WeatherState
    data class Error(val reason: ErrorReason) : WeatherState
}

enum class ErrorReason { NETWORK, SERVER, LOCATION_DENIED, LOCATION_UNAVAILABLE }
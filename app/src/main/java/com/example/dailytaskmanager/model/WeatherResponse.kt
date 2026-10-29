package com.example.dailytaskmanager.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(val current: Current, val daily: Daily) {
    @Serializable
    data class Current(
        @SerialName("temperature_2m") val temperature: Double,
        @SerialName("weather_code") val code: Int
    )
    @Serializable
    data class Daily(
        @SerialName("temperature_2m_max") val max: List<Double>? = emptyList(),
        @SerialName("temperature_2m_min") val min: List<Double>? = emptyList(),
        @SerialName("precipitation_probability_max") val rain: List<Int?> = emptyList()
    )
}

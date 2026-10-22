package com.example.dailytaskmanager.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Serializable
data class WeatherResponse(val current: Current, val daily: Daily) {
    @Serializable
    data class Current(
        @SerialName("temperature_2m") val temperature: Double,
        @SerialName("weather_code") val code: Int
    )
    @Serializable
    data class Daily(
        @SerialName("temperature_2m_max") val max: List<Double>,
        @SerialName("temperature_2m_min") val min: List<Double>,
        @SerialName("precipitation_probability_max") val rain: List<Int>
    )
}

class WeatherRepository(private val client: HttpClient) {
    suspend fun get(lat: Double, lon: Double): Result<WeatherResponse> = runCatching {
        client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", lat)
            parameter("longitude", lon)
            parameter("current", "temperature_2m,weather_code")
            parameter("daily", "temperature_2m_max,temperature_2m_min,precipitation_probability_max")
            parameter("timezone", "auto")
            parameter("forecast_days", 1)
        }.body()
    }
}
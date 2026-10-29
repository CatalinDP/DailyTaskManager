package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.model.ErrorReason
import com.example.dailytaskmanager.model.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class WeatherRepository(private val client: HttpClient) {

    class WeatherException(val reason: ErrorReason, cause: Throwable) : Exception(cause)

    suspend fun get(lat: Double, lon: Double): Result<WeatherResponse> = try {
        Result.success(
            client.get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", lat)
                parameter("longitude", lon)
                parameter("current", "temperature_2m,weather_code")
                parameter("daily", "temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                parameter("timezone", "auto")
                parameter("forecast_days", 1)
            }.body()
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.w(e, "Weather request failed")
        // Connection problems and timeouts are IOExceptions; HTTP errors and bad payloads are the service's fault
        val reason = if (e is IOException) ErrorReason.NETWORK else ErrorReason.SERVER
        Result.failure(WeatherException(reason, e))
    }
}

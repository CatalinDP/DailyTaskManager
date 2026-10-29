package com.example.dailytaskmanager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.toDisplay
import com.example.dailytaskmanager.model.WeatherResponse

@Composable
fun WeatherComponent(
    weather: WeatherResponse,
    unit: TempUnit
) {
    val current = weather.current
    val daily = weather.daily
    val temperature = weather.current.temperature.toDisplay(unit)
    val maxTemp = weather.daily.max?.firstOrNull()?.toDisplay(unit)
    val minTemp = weather.daily.min?.firstOrNull()?.toDisplay(unit)
    val rain = weather.daily.rain.firstOrNull()
    val unitString = stringResource(
        if (unit == TempUnit.FAHRENHEIT) R.string.temp_unit_fahrenheit else R.string.temp_unit_celsius
    )
    val code = current.code
    val icon = weatherIcon(code)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.inversePrimary, RoundedCornerShape(16.dp))
            .clip(shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Icon(
            icon,
            contentDescription = stringResource(R.string.weather_icon_description),
            modifier = Modifier
                .size(24.dp)
                .padding(start = 8.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.weather_today_forecast),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
            )
            Text(
                text = stringResource(R.string.weather_current_temperature, temperature),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = rain?.let { stringResource(R.string.weather_rain_probability, it) } ?: "—",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.weather_max, maxTemp?.toString() ?: "—", unitString),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(R.string.weather_min, minTemp?.toString() ?: "—", unitString),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

private fun weatherIcon(code: Int): ImageVector = when (code) {
    0 -> Icons.Filled.WbSunny
    1, 2, 3 -> Icons.Filled.WbCloudy
    45, 48 -> Icons.Filled.Cloud
    in 51..67, in 80..82 -> Icons.Filled.Umbrella
    in 71..77, 85, 86 -> Icons.Filled.AcUnit
    in 95..99 -> Icons.Filled.Thunderstorm
    else -> Icons.Filled.Cloud
}
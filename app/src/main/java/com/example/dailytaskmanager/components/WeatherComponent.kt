package com.example.dailytaskmanager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.toDisplay
import com.example.dailytaskmanager.weather.WeatherResponse

@Composable
fun WeatherComponent(
    weather: WeatherResponse,
    unit: TempUnit
) {
    val current = weather.current
    val daily = weather.daily
    var temperature = current.temperature.toInt()
    var maxTemp = daily.max.map { it.toInt() }
    var minTemp = daily.min.map { it.toInt()  }
    val rain = daily.rain.max()
    val code = current.code
    val icon = weatherIcon(code)
    if (unit == TempUnit.FAHRENHEIT) {
        temperature = current.temperature.toDisplay(unit)
        maxTemp = daily.max.map { it.toDisplay(unit)  }
        minTemp = daily.min.map { it.toDisplay(unit)  }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Icon(
            icon,
            contentDescription = stringResource(R.string.weather_icon_description),
            modifier = Modifier
                .size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.weather_today_forecast),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.surface
            )
            Text(
                text = stringResource(R.string.weather_current_temperature, temperature),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.weather_rain_probability, rain),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "$maxTemp",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.surface
            )
            Text(
                text = "$minTemp",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
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
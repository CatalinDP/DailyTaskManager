package com.example.dailytaskmanager.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputComponent(
    //TODO: Make time work
    showEndTime: Boolean = false,
    startTimeState: TimePickerState,
    endTimeState: TimePickerState? = null,
    startText: String,
    endText: String = " --- "
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = startText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TimeInput(
            state = startTimeState,
            modifier = Modifier
                .padding(vertical = 8.dp),
            colors = appTimePickerColors()
        )
        if (showEndTime && endTimeState != null) {
            Text(
                text = endText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TimeInput(
                state = endTimeState,
                modifier = Modifier
                    .padding(vertical = 8.dp),
                colors = appTimePickerColors()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun appTimePickerColors() = TimePickerDefaults.colors(
    clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
    clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
    selectorColor = MaterialTheme.colorScheme.primary,
    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
)
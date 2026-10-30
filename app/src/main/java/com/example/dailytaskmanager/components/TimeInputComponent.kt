package com.example.dailytaskmanager.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.dailytaskmanager.utils.appTimePickerColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputComponent(
    showEndTime: Boolean = false,
    startTimeState: TimePickerState,
    endTimeState: TimePickerState? = null,
    startText: String,
    endText: String = " --- "
) {
    // TimeInput requests focus for its hour field as soon as it is composed, which opens the
    // keyboard on every screen that shows it. Focus is only allowed once the user touches it.
    var userTouched by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    userTouched = true
                }
            }
            .focusProperties { canFocus = userTouched }
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
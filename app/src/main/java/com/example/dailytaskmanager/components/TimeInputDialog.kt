package com.example.dailytaskmanager.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable

/**
 * Dialog with one time input, or two when [endState] is given.
 * The caller owns the picker states and validation: pass [errorText] to show an error and
 * disable the confirm button, and read the states in [onConfirm].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputDialog(
    title: String,
    startState: TimePickerState,
    startText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    endState: TimePickerState? = null,
    endText: String = "",
    errorText: String? = null,
) {
    AppDialog(
        title = title,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmEnabled = errorText == null
    ) {
        TimeInputComponent(
            showEndTime = endState != null,
            startTimeState = startState,
            endTimeState = endState,
            startText = startText,
            endText = endText
        )
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

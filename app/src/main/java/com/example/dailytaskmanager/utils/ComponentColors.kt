package com.example.dailytaskmanager.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable

@Composable
fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    errorTextColor = MaterialTheme.colorScheme.error
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appTimePickerColors() = TimePickerDefaults.colors(
    clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
    clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
    selectorColor = MaterialTheme.colorScheme.primary,
    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
)
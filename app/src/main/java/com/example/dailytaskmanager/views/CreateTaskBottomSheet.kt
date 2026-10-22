package com.example.dailytaskmanager.views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.components.TimeInputComponent
import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.notifications.rememberNotificationpermissionRequester
import com.example.dailytaskmanager.ui.theme.DailyTaskManagerTheme
import com.example.dailytaskmanager.viewmodel.CreateTaskViewModel
import com.example.dailytaskmanager.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskBottomSheet(
    onDismiss: () -> Unit,
    task: Task? = null,
    viewModel: CreateTaskViewModel = koinViewModel()
    ) {
    var allowFocus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (task != null) {
            viewModel.editTask(task)
        }
        withFrameNanos {  }
        allowFocus = true
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val timePickerState = rememberTimePickerState(is24Hour = true)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Log.d("COMPLETION", "${state.taskStartTime.hour}")
    val startTimeState = rememberTimePickerState(initialHour = state.taskStartTime.hour, initialMinute = state.taskStartTime.minute, is24Hour = true)
    val endTimeState = rememberTimePickerState(initialHour = state.taskEndTime.hour, initialMinute = state.taskEndTime.minute, is24Hour = true)
    val scrollState = rememberScrollState()
    var showTimeDialog by remember { mutableStateOf(false) }

    val closeSheet: (onFinished: () -> Unit) -> Unit = { onFinished ->
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onFinished()
            }
        }
    }

    val requestNotificationPermission = rememberNotificationpermissionRequester { _ ->
        viewModel.onEndTimeChange(LocalTime.of(endTimeState.hour, endTimeState.minute))
        viewModel.onStartTimeChange(LocalTime.of(startTimeState.hour, startTimeState.minute))
        if (task == null) {
            viewModel.saveTask(onSaved = onDismiss)
            Log.d("COMPLETION", "SAVE")
        } else {
            viewModel.updateTask(onUpdated = { onDismiss()
                Log.d("COMPLETION", "OnUpdate")})
            Log.d("COMPLETION", "Edit")
        }
    }

    ModalBottomSheet(
        onDismissRequest = { closeSheet {
            onDismiss()
            viewModel.resetUiState()
        } },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
                .focusProperties { canFocus = allowFocus }
        ) {
            Text(
                text = stringResource(R.string.create_task_title),
                style = MaterialTheme.typography.titleLarge
            )
            Column() {
                Text(
                    text = stringResource(R.string.create_task_name_title),
                    modifier = Modifier
                        .imePadding()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                    )
                OutlinedTextField(
                    value = state.taskName,
                    onValueChange = viewModel::onNameChange,
                    label = { Text(stringResource(R.string.create_task_name_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
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
                    ),
                    maxLines = 1
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimeInputComponent(
                    startTimeState = startTimeState,
                    endTimeState = endTimeState,
                    startText = stringResource(R.string.create_task_start_time),
                    endText = stringResource(R.string.create_task_end_time),
                    showEndTime = true
                )
            }
            Column {
                Text(stringResource(R.string.create_task_repetition_days))
                DayPicker(
                    selectedDays = state.taskRepetitionDays,
                    onSelectedDaysChange = viewModel::onDayToggled
                )
            }
            TextButton(
                onClick = {
                    showTimeDialog = true
                }
            ) {
                Text(text = stringResource(R.string.create_task_select_time))
            }

            if (showTimeDialog) {
                TimePickerDialog(
                    onDismissRequest = { showTimeDialog = false },
                    title = { Text(stringResource(R.string.create_task_time_dialog_title)) },
                    confirmButton = {
                        TextButton(onClick = {
                            // Acceder a timePickerState.hour y timePickerState.minute
                            showTimeDialog = false
                        }) {
                            Text(stringResource(R.string.common_accept))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimeDialog = false }) {
                            Text(stringResource(R.string.common_cancel))
                        }
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }

            Button(
                onClick = {
                    requestNotificationPermission()
                          },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = if (task == null) stringResource(R.string.create_task_create_button) else stringResource(R.string.create_task_edit_button))
            }

        }
    }
}
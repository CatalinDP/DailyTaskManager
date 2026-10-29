package com.example.dailytaskmanager.viewmodel

import timber.log.Timber
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.repository.TaskReminderUseCase
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.uistate.CreateTaskUiState
import com.example.dailytaskmanager.uistate.CreateTaskValidationError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class CreateTaskViewModel(
    private val repository: TaskRepository,
    private val reminders: TaskReminderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun resetUiState() = _uiState.update { CreateTaskUiState() }
    fun onNameChange(taskName: String) = _uiState.update { it.copy(taskName = taskName) }

    fun onDayToggled(day: Day) = _uiState.update {
        val days = if (day in it.taskRepetitionDays) it.taskRepetitionDays - day else it.taskRepetitionDays + day
        Timber.d("days: $days")
        it.copy(taskRepetitionDays = days)
    }

    fun onStartTimeChange(startTime: LocalTime) = _uiState.update { it.copy(taskStartTime = startTime) }
    fun onEndTimeChange(endTime: LocalTime) = _uiState.update { it.copy(taskEndTime = endTime) }

    fun editTask(task: Task) {
        _uiState.update {
            it.copy(
                taskId = task.id,
                taskName = task.name,
                taskStartTime = task.startingTime,
                taskEndTime = task.endingTime,
                taskRepetitionDays = task.repetitionDays.toSet(),
            )
        }
    }

    fun saveTask(onSaved: () -> Unit, onError: (CreateTaskValidationError) -> Unit) {
        val state = _uiState.value
        state.validationError?.let { onError(it); return }
        viewModelScope.launch {
            reminders.insertTask(
                Task(
                    name = state.taskName.trim(),
                    startingTime = state.taskStartTime,
                    endingTime = state.taskEndTime,
                    repetitionDays = state.taskRepetitionDays.toList().sortedBy { it.ordinal },
                )
            )
            _uiState.value = CreateTaskUiState()
            onSaved()
        }
    }

    fun updateTask(onUpdated: () -> Unit, onError: (CreateTaskValidationError) -> Unit) {
        val state = _uiState.value
        state.validationError?.let { onError(it); return }
        viewModelScope.launch {
            // Deleted meanwhile (e.g. from another screen): nothing left to edit, just close
            val original = repository.getTaskById(state.taskId)
            if (original != null) {
                reminders.updateTask(
                    original.copy(
                        name = state.taskName.trim(),
                        startingTime = state.taskStartTime,
                        endingTime = state.taskEndTime,
                        repetitionDays = state.taskRepetitionDays.toList().sortedBy { it.ordinal },
                    )
                )
            }
            _uiState.value = CreateTaskUiState()
            onUpdated()
        }
    }
}

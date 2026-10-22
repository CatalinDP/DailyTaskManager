package com.example.dailytaskmanager.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.uiState.CreateTaskUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class CreateTaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun resetUiState() = _uiState.update { CreateTaskUiState() }
    fun onNameChange(taskName: String) = _uiState.update { it.copy(taskName = taskName) }

    fun onDayToggled(day: Day) = _uiState.update {
        val days = if (day in it.taskRepetitionDays) it.taskRepetitionDays - day else it.taskRepetitionDays + day
        Log.d("COMPLETION", days.toString())
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

    fun saveTask(onSaved: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            repository.insertTask(
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

    fun updateTask(onUpdated: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            repository.updateTask(
                Task(
                    id = state.taskId,
                    name = state.taskName.trim(),
                    startingTime = state.taskStartTime,
                    endingTime = state.taskEndTime,
                    repetitionDays = state.taskRepetitionDays.toList().sortedBy { it.ordinal },
                )
            )
            _uiState.value = CreateTaskUiState()
            onUpdated()
        }
    }
}
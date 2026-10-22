package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.utils.getCurrentDayName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val pendingDeleteIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _completedIds = MutableStateFlow<List<Long>>(emptyList())
    val completedIds = _completedIds.asStateFlow()
    private val _selectedDay = MutableStateFlow<Day>(getCurrentDayName())
    val selectedDay = _selectedDay.asStateFlow()
    val selectedDayTasks: StateFlow<List<Task>> =
        combine(repository.getAllTasks(), pendingDeleteIds, _selectedDay) { tasks, pendingDeleteIds, selectedDay ->
            tasks.filter { selectedDay in it.repetitionDays && it.id !in pendingDeleteIds }
                .sortedBy { it.startingTime }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getNextDay() {
        _selectedDay.update { Day.entries[(_selectedDay.value.ordinal + 1) % Day.entries.size] }
    }

    fun getPreviousDay() {
        _selectedDay.update { Day.entries[(_selectedDay.value.ordinal - 1) % Day.entries.size] }
    }

    fun requestDelete(task: Task) {
        pendingDeleteIds.update { it + task.id }
        repository.cancelReminder(task)
    }

    fun undoDelete(task: Task) {
        pendingDeleteIds.update { it - task.id }
        repository.scheduleReminder(task)
    }

    fun commitDelete(task: Task) {
        if (task.id !in pendingDeleteIds.value) return
        viewModelScope.launch {
            repository.deleteTask(task)
            pendingDeleteIds.update { it - task.id }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun getCompletedIds() {
        viewModelScope.launch {
            repository.completedIds(LocalDate.now()).collect {
                _completedIds.value = it
            }
        }
    }

    fun onTaskChecked(task: Task, checked: Boolean) {
        viewModelScope.launch {
            repository.setCompleted(task.id, LocalDate.now(), checked)
            getCompletedIds()
        }
    }
}
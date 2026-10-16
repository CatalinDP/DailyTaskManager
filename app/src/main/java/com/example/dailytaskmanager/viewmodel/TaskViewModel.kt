package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.utils.getCurrentDayName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val pendingDeleteIds = MutableStateFlow<Set<Long>>(emptySet())
    val todayTaskList: StateFlow<List<Task>> = repository.getAllTasks()
        .map { tasks ->
            val today = getCurrentDayName()
            tasks.filter { today in it.repetitionDays }
                .sortedBy { it.startingTime }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun requestDelete(task: Task) {
        pendingDeleteIds.update { it + task.id }
        repository.cancelReminder(task)
    }

    fun undoDelete(task: Task) {
        pendingDeleteIds.update { it - task.id }
        repository.scheduleReminder(task)
        restoreTask(task)
    }

    fun commitDelete(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            pendingDeleteIds.update { it - task.id }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch { repository.insertTask(task) }
    }
}
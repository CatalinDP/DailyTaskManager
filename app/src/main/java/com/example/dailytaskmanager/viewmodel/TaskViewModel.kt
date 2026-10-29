package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.repository.TaskReminderUseCase
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.utils.calculateStreak
import com.example.dailytaskmanager.utils.getCurrentDayName
import com.example.dailytaskmanager.utils.toDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.time.Duration.Companion.milliseconds

class TaskViewModel(
    private val repository: TaskRepository,
    private val reminders: TaskReminderUseCase
    ) : ViewModel() {
    private val pendingDeleteIds = MutableStateFlow<Set<Long>>(emptySet())
    // One Room subscription shared by every derived flow below
    private val tasks = repository.getAllTasks()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)
    private val today: Flow<LocalDate> = flow {
        while (true) { emit(LocalDate.now()); delay(60_000.milliseconds) }
    }.distinctUntilChanged()
    val completedIds: StateFlow<Set<Long>> = today
        .flatMapLatest { date -> repository.completedIds(date) }
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())
    private val _selectedDay = MutableStateFlow<Day>(getCurrentDayName())
    val selectedDay = _selectedDay.asStateFlow()
    val selectedDayTasks: StateFlow<List<Task>> =
        combine(tasks, pendingDeleteIds, _selectedDay) { tasks, pendingDeleteIds, selectedDay ->
            tasks.filter { selectedDay in it.repetitionDays && it.id !in pendingDeleteIds }
                .sortedBy { it.startingTime }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val weekProgression: StateFlow<List<Float?>> = today.flatMapLatest { date ->
        val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekDates = (0..6).map { weekStart.plusDays(it.toLong()) }
        combine(
            tasks,
            repository.completionDates(weekDates.first(), weekDates.last())
        ) { tasks, completions ->
            weekDates.map { day ->
                if (day.isAfter(date)) return@map null
                val dayName = day.toDay()
                val scheduledIds = tasks
                    .filter { dayName in it.repetitionDays && day >= it.createdAt }
                    .map { it.id }
                    .toSet()
                if (scheduledIds.isEmpty()) null
                else completions.count { it.date == day && it.taskId in scheduledIds }.toFloat() / scheduledIds.size
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), List(7) { null })

    val dayStreak: StateFlow<Int> = today.flatMapLatest { date ->
        combine(
            tasks,
            repository.completionDates(date.minusDays(365), date)
        ) { tasks, completions ->
            calculateStreak(tasks, completions, date)
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)


    fun getNextDay() {
        _selectedDay.update { Day.entries[(it.ordinal + 1) % Day.entries.size] }
    }

    fun getPreviousDay() {
        _selectedDay.update { Day.entries[Math.floorMod(it.ordinal - 1, Day.entries.size)] }
    }

    fun requestDelete(task: Task) {
        pendingDeleteIds.update { it + task.id }
    }

    fun undoDelete(task: Task) {
        pendingDeleteIds.update { it - task.id }
    }

    fun commitDelete(task: Task) {
        if (task.id !in pendingDeleteIds.value) return
        viewModelScope.launch {
            reminders.deleteTask(task)
            pendingDeleteIds.update { it - task.id }
        }
    }

    fun onTaskChecked(task: Task, checked: Boolean) {
        viewModelScope.launch {
            repository.setCompleted(task.id, LocalDate.now(), checked)
        }
    }
}

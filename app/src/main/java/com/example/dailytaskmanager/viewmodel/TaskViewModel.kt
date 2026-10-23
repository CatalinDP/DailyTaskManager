package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.utils.calculateStreak
import com.example.dailytaskmanager.utils.getCurrentDayName
import io.ktor.util.Hash.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

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

    private val today = LocalDate.now()
    private val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private val weekDates = (0..6).map { weekStart.plusDays(it.toLong()) }

    val weekProgression: StateFlow<List<Float?>> = combine(
        repository.getAllTasks(),
        repository.completionDates(weekDates.first(), weekDates.last())
    ) { tasks, completions ->
        weekDates.map { date ->
            if (date.isAfter(today)) return@map null
            val day = Day.valueOf(date.dayOfWeek.name)
            val scheduledIds = tasks.filter { day in it.repetitionDays && date >= it.createdAt }
                .map { it.id }
                .toSet()

            if (scheduledIds.isEmpty()) null
            else completions.count { it.date == date && it.taskId in scheduledIds }.toFloat() / scheduledIds.size
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), List(7) { null })

    val dayStreak: StateFlow<Int> = combine(
        repository.getAllTasks(),
        repository.allCompletions()
    ) { tasks, completions ->
        calculateStreak(tasks, completions, today)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)


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

object DebugSeeder {

    suspend fun seed(repository: TaskRepository) {
        if (repository.getAllTasks().first().isNotEmpty()) return

        val today = LocalDate.now()
        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val everyDay = Day.entries
        val workdays = Day.entries.take(5)
        val weekend = Day.entries.drop(5)

        listOf(
            Task(name = "Meditar", startingTime = LocalTime.of(7, 0), endingTime = LocalTime.of(7, 15),
                repetitionDays = everyDay, createdAt = weekStart),
            Task(name = "Tomar vitaminas", startingTime = LocalTime.of(8, 30), endingTime = LocalTime.of(8, 40),
                repetitionDays = everyDay, createdAt = weekStart),
            Task(name = "Estudiar Kotlin", startingTime = LocalTime.of(10, 0), endingTime = LocalTime.of(12, 0),
                repetitionDays = workdays, createdAt = weekStart),
            Task(name = "Ir al gimnasio", startingTime = LocalTime.of(18, 0), endingTime = LocalTime.of(19, 30),
                repetitionDays = listOf(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY), createdAt = weekStart),
            Task(name = "Leer", startingTime = LocalTime.of(22, 0), endingTime = LocalTime.of(22, 30),
                repetitionDays = everyDay, createdAt = weekStart),
            Task(name = "Comprar la semana", startingTime = LocalTime.of(11, 0), endingTime = LocalTime.of(12, 0),
                repetitionDays = weekend, createdAt = weekStart)
        ).forEach { repository.insertTask(it) }

        // Completados en los días ya pasados de esta semana (para las estadísticas)
        val tasks = repository.getAllTasks().first()
        (0..6).map { weekStart.plusDays(it.toLong()) }
            .filter { it.isBefore(today) }
            .forEach { date ->
                val day = Day.valueOf(date.dayOfWeek.name)
                tasks.filter { day in it.repetitionDays }
                    .forEachIndexed { index, task ->
                        if ((index + date.dayOfMonth) % 3 != 0) {
                            repository.setCompleted(task.id, date, true)
                        }
                    }
            }
    }
}
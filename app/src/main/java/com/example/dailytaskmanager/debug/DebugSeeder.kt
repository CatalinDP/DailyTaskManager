package com.example.dailytaskmanager.debug

import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.repository.TaskReminderUseCase
import com.example.dailytaskmanager.repository.TaskRepository
import com.example.dailytaskmanager.utils.toDay
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

/** Fills an empty database with sample tasks and completions. Only called from debug builds. */
object DebugSeeder {

    suspend fun seed(repository: TaskRepository, reminders: TaskReminderUseCase) {
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
        ).forEach { reminders.insertTask(it) }

        // Completados en los días ya pasados de esta semana (para las estadísticas)
        val tasks = repository.getAllTasks().first()
        (0..6).map { weekStart.plusDays(it.toLong()) }
            .filter { it.isBefore(today) }
            .forEach { date ->
                val day = date.toDay()
                tasks.filter { day in it.repetitionDays }
                    .forEachIndexed { index, task ->
                        if ((index + date.dayOfMonth) % 3 != 0) {
                            repository.setCompleted(task.id, date, true)
                        }
                    }
            }
    }
}
package com.example.dailytaskmanager.utils

import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.TaskCompletion
import java.time.LocalDate

fun calculateStreak(
    tasks: List<Task>,
    completions: List<TaskCompletion>,
    today: LocalDate
): Int {
    if (tasks.isEmpty()) return 0

    val earliest = tasks.minOf { it.createdAt }
    val doneByDate: Map<LocalDate, Set<Long>> = completions
        .groupBy({ it.date }, { it.taskId })
        .mapValues { it.value.toSet() }

    fun scheduledIds(date: LocalDate): Set<Long> {
        val day = Day.valueOf(date.dayOfWeek.name)
        return tasks
            .filter { day in it.repetitionDays && it.createdAt <= date }
            .map { it.id }
            .toSet()
    }

    var streak = 0
    var date = today
    while (!date.isBefore(earliest)) {
        val scheduled = scheduledIds(date)
        if (scheduled.isNotEmpty()) {
            val done = doneByDate[date].orEmpty().any { it in scheduled }
            if (done) streak++
            else if (date != today) break   // hoy sin completar aún no rompe la racha
        }
        date = date.minusDays(1)
    }
    return streak
}
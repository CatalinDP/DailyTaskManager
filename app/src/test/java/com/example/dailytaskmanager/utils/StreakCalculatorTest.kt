package com.example.dailytaskmanager.utils

import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.model.TaskCompletion
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class StreakCalculatorTest {

    // 2026-10-28 is a Wednesday
    private val today = LocalDate.of(2026, 10, 28)

    private fun task(
        id: Long,
        days: List<Day> = Day.entries,
        createdAt: LocalDate = today.minusDays(30)
    ) = Task(
        id = id,
        name = "Task $id",
        startingTime = LocalTime.of(9, 0),
        endingTime = LocalTime.of(10, 0),
        repetitionDays = days,
        createdAt = createdAt
    )

    private fun doneOn(taskId: Long, vararg daysAgo: Long) =
        daysAgo.map { TaskCompletion(taskId, today.minusDays(it)) }

    @Test
    fun calculateStreak_noTasks_isZero() {
        assertEquals(0, calculateStreak(emptyList(), emptyList(), today))
    }

    @Test
    fun calculateStreak_consecutiveDaysIncludingToday_countsAll() {
        val completions = doneOn(1, 0, 1, 2)
        assertEquals(3, calculateStreak(listOf(task(1)), completions, today))
    }

    @Test
    fun calculateStreak_todayIncomplete_doesNotBreak() {
        val completions = doneOn(1, 1, 2)
        assertEquals(2, calculateStreak(listOf(task(1)), completions, today))
    }

    @Test
    fun calculateStreak_missedDay_breaks() {
        // Done today and 2 days ago, missed yesterday
        val completions = doneOn(1, 0, 2, 3)
        assertEquals(1, calculateStreak(listOf(task(1)), completions, today))
    }

    @Test
    fun calculateStreak_oneOfSeveralTasksDone_countsTheDay() {
        val completions = doneOn(2, 0, 1)
        assertEquals(2, calculateStreak(listOf(task(1), task(2)), completions, today))
    }

    @Test
    fun calculateStreak_daysWithoutScheduledTasks_areSkipped() {
        // Only on Wednesdays: today and one week ago, nothing scheduled in between
        val wednesdays = listOf(task(1, days = listOf(Day.WEDNESDAY)))
        val completions = doneOn(1, 0, 7)
        assertEquals(2, calculateStreak(wednesdays, completions, today))
    }

    @Test
    fun calculateStreak_ignoresDaysBeforeCreatedAt() {
        // Created yesterday: the days before cannot break or extend the streak
        val tasks = listOf(task(1, createdAt = today.minusDays(1)))
        val completions = doneOn(1, 0, 1, 5)
        assertEquals(2, calculateStreak(tasks, completions, today))
    }
}

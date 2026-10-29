package com.example.dailytaskmanager.utils

import com.example.dailytaskmanager.model.Day
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class TriggerTimeTest {

    // 2026-10-26 is a Monday
    private val mondayAt10 = LocalDateTime.of(2026, 10, 26, 10, 0)

    @Test
    fun nextTriggerTime_sameDayLater_returnsToday() {
        val result = nextTriggerTime(listOf(Day.MONDAY), LocalTime.of(18, 0), mondayAt10)
        assertEquals(LocalDateTime.of(2026, 10, 26, 18, 0), result)
    }

    @Test
    fun nextTriggerTime_sameDayPassed_returnsNextWeek() {
        val result = nextTriggerTime(listOf(Day.MONDAY), LocalTime.of(9, 0), mondayAt10)
        assertEquals(LocalDateTime.of(2026, 11, 2, 9, 0), result)
    }

    @Test
    fun nextTriggerTime_exactlyNow_isNotReturned() {
        val result = nextTriggerTime(listOf(Day.MONDAY), LocalTime.of(10, 0), mondayAt10)
        assertEquals(LocalDateTime.of(2026, 11, 2, 10, 0), result)
    }

    @Test
    fun nextTriggerTime_picksClosestOfSeveralDays() {
        val result = nextTriggerTime(listOf(Day.FRIDAY, Day.WEDNESDAY), LocalTime.of(9, 0), mondayAt10)
        assertEquals(LocalDateTime.of(2026, 10, 28, 9, 0), result)
    }

    @Test
    fun nextTriggerTime_sundayToMonday_crossesWeek() {
        val sundayNight = LocalDateTime.of(2026, 11, 1, 23, 0)
        val result = nextTriggerTime(listOf(Day.MONDAY), LocalTime.of(7, 0), sundayNight)
        assertEquals(LocalDateTime.of(2026, 11, 2, 7, 0), result)
    }

    @Test
    fun nextTriggerTime_emptyDays_returnsNull() {
        assertNull(nextTriggerTime(emptyList(), LocalTime.of(9, 0), mondayAt10))
    }
}

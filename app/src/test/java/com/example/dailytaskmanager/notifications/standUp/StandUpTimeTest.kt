package com.example.dailytaskmanager.notifications.standUp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class StandUpTimeTest {

    private val start = LocalTime.of(9, 0)
    private val end = LocalTime.of(21, 0)
    private fun at(hour: Int, minute: Int = 0) = LocalDateTime.of(2026, 10, 26, hour, minute)

    @Test
    fun nextStandUpTime_beforeWindow_returnsWindowStart() {
        assertEquals(at(9), nextStandUpTime(2, start, end, at(7)))
    }

    @Test
    fun nextStandUpTime_insideWindow_returnsNextSlot() {
        // Slots every 2h from 09:00: 09, 11, 13...
        assertEquals(at(11), nextStandUpTime(2, start, end, at(10, 30)))
    }

    @Test
    fun nextStandUpTime_onSlot_returnsFollowingSlot() {
        assertEquals(at(13), nextStandUpTime(2, start, end, at(11)))
    }

    @Test
    fun nextStandUpTime_lastSlotEqualToEnd_isIncluded() {
        assertEquals(at(21), nextStandUpTime(4, start, end, at(18)))
    }

    @Test
    fun nextStandUpTime_afterWindow_returnsTomorrowStart() {
        assertEquals(LocalDateTime.of(2026, 10, 27, 9, 0), nextStandUpTime(2, start, end, at(22)))
    }

    @Test
    fun nextStandUpTime_invalidWindow_returnsNull() {
        assertNull(nextStandUpTime(2, end, start, at(10)))
        assertNull(nextStandUpTime(2, start, start, at(10)))
    }

    @Test
    fun nextStandUpTime_nonPositiveInterval_returnsNull() {
        assertNull(nextStandUpTime(0, start, end, at(10)))
    }
}

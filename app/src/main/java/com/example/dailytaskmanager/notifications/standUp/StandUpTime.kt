package com.example.dailytaskmanager.notifications.standUp

import java.time.LocalDateTime
import java.time.LocalTime

fun nextStandUpTime(
    intervalHours: Int,
    windowStart: LocalTime,
    windowEnd: LocalTime,
    now: LocalDateTime = LocalDateTime.now()
): LocalDateTime? {
    if (intervalHours <= 0 || !windowStart.isBefore(windowEnd)) return null

    for (dayOffset in 0L..1L) {
        val date = now.toLocalDate().plusDays(dayOffset)
        val end = date.atTime(windowEnd)
        var slot = date.atTime(windowStart)
        while (!slot.isAfter(end)) {
            if (slot.isAfter(now)) return slot
            slot = slot.plusHours(intervalHours.toLong())
        }
    }
    return null
}
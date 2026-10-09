package com.example.dailytaskmanager.notifications

import com.example.dailytaskmanager.model.Day
import java.time.LocalDateTime
import java.time.LocalTime

fun nextTriggerTime(
    days: List<Day>,
    time: LocalTime,
    now: LocalDateTime = LocalDateTime.now()
): LocalDateTime? {
    if (days.isEmpty()) return null
    for (offset in 0L..7L) {
        val date = now.toLocalDate().plusDays(offset)
        val candidate = date.atTime(time)
        if (Day.valueOf(date.dayOfWeek.name) in days && candidate.isAfter(now)) {
            return candidate
        }
    }
    return null
}
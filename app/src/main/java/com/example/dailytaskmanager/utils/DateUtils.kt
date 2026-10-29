package com.example.dailytaskmanager.utils

import android.text.format.DateFormat
import com.example.dailytaskmanager.model.Day
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun getFormattedCurrentDate(): String {
    val locale = Locale.getDefault()
    val pattern = DateFormat.getBestDateTimePattern(locale, "EEEEdMMMM")
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)

    return LocalDate.now().format(formatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}

fun getCurrentDayName(): Day {
    return LocalDate.now().toDay()
}

fun getCurrentDayNumber(): Int {
    return LocalDate.now().dayOfWeek.value - 1
}

fun LocalDate.toDay(): Day = Day.valueOf(dayOfWeek.name)

/** One-letter week day labels starting on Monday, e.g. "L M X J V S D" in Spanish. */
fun weekDayNarrowLabels(locale: Locale = Locale.getDefault()): List<String> =
    Day.entries.map { DayOfWeek.valueOf(it.name).getDisplayName(TextStyle.NARROW, locale) }

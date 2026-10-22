package com.example.dailytaskmanager.utils

import android.text.format.DateFormat
import com.example.dailytaskmanager.model.Day
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun getFormattedCurrentDate(): String {
    val locale = Locale.getDefault()
    val pattern = DateFormat.getBestDateTimePattern(locale, "EEEEdMMMM")
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)

    return LocalDate.now().format(formatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}

fun getFormattedSelectedDate(): String {
    val locale = Locale.getDefault()
    val pattern = DateFormat.getBestDateTimePattern(locale, "EEEEdMMMM")
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)

    return LocalDate.now().format(formatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}

fun getCurrentDayName(): Day {
    return Day.valueOf(LocalDate.now().dayOfWeek.name)
}


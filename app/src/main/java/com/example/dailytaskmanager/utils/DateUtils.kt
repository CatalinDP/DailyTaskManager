package com.example.dailytaskmanager.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dailytaskmanager.model.Day
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val today = LocalDate.now()
private val localeEs = Locale.forLanguageTag("es-ES")
private val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", localeEs)

fun getFormattedCurrentDate(): String {

    return today.format(formatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun getCurrentDayName(): Day {
    return Day.valueOf(LocalDate.now().dayOfWeek.name)
}
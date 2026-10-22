package com.example.dailytaskmanager.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.toString();
    }

    @TypeConverter
    fun toLocalTime(time: String?): LocalTime? {
        return time?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromDayList(days: List<Day>?): String? {
        if (days.isNullOrEmpty()) return ""
        return days.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayList(data: String?): List<Day> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split(",").mapNotNull { name ->
            runCatching { Day.valueOf(name) }.getOrNull()
        }
    }

    @TypeConverter
    fun fromDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toDate(value: String): LocalDate = LocalDate.parse(value)
}
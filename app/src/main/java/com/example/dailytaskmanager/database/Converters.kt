package com.example.dailytaskmanager.database

import androidx.room.TypeConverter
import com.example.dailytaskmanager.model.Day
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    // Nullable because User.startingTime is a nullable column; Task's times are not
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? = time?.toString()

    @TypeConverter
    fun toLocalTime(time: String?): LocalTime? = time?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromDayList(days: List<Day>): String = days.joinToString(",") { it.name }

    @TypeConverter
    fun toDayList(data: String): List<Day> {
        if (data.isBlank()) return emptyList()
        return data.split(",").mapNotNull { name ->
            runCatching { Day.valueOf(name) }.getOrNull()
        }
    }

    @TypeConverter
    fun fromDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toDate(value: String): LocalDate = LocalDate.parse(value)
}

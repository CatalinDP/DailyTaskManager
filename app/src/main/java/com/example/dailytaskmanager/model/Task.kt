package com.example.dailytaskmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isCompleted: Boolean = false,

    val startingTime: LocalTime?,
    val endingTime: LocalTime?,
    val repetitionDays: List<Day>
)
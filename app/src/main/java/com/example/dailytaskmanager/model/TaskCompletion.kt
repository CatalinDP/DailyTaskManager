package com.example.dailytaskmanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate

@Entity(
    tableName = "task_completion",
    primaryKeys = ["taskId", "date"],
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskCompletion(
    val taskId: Long,
    val date: LocalDate
)
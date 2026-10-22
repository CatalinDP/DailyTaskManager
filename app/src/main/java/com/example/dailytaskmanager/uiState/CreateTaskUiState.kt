package com.example.dailytaskmanager.uiState

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.dailytaskmanager.model.Day
import java.time.LocalTime

data class CreateTaskUiState(
    var taskId: Long = 0,
    var taskName: String = "",
    var taskStartTime: LocalTime = LocalTime.of(8, 0),
    var taskEndTime: LocalTime = LocalTime.of(10, 0),
    var taskRepetitionDays: Set<Day> = emptySet(),
    var isCompleted: Boolean = false
) {
    val canSave: Boolean get() = taskName.isNotBlank() && taskRepetitionDays.isNotEmpty()
}
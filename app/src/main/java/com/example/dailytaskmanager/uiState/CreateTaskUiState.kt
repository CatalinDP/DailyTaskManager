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
    var taskName: String = "",
    var taskStartTime: LocalTime = LocalTime.of(12, 0),
    var taskEndTime: LocalTime = LocalTime.of(14, 0),
    var taskRepetitionDays: Set<Day> = emptySet()
) {
    val canSave: Boolean get() = taskName.isNotBlank() && taskRepetitionDays.isNotEmpty()
}
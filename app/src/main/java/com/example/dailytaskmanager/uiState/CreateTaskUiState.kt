package com.example.dailytaskmanager.uistate

import com.example.dailytaskmanager.model.Day
import java.time.LocalTime

data class CreateTaskUiState(
    val taskId: Long = 0,
    val taskName: String = "",
    val taskStartTime: LocalTime = LocalTime.of(8, 0),
    val taskEndTime: LocalTime = LocalTime.of(10, 0),
    val taskRepetitionDays: Set<Day> = emptySet()
) {
    /** First reason the task can't be saved, or null when it is valid. */
    val validationError: CreateTaskValidationError?
        get() = when {
            taskName.isBlank() -> CreateTaskValidationError.NAME_EMPTY
            taskRepetitionDays.isEmpty() -> CreateTaskValidationError.NO_DAYS
            // Tasks crossing midnight are not supported: notifications only live within one day
            !taskStartTime.isBefore(taskEndTime) -> CreateTaskValidationError.END_NOT_AFTER_START
            else -> null
        }

    val canSave: Boolean get() = validationError == null
}

enum class CreateTaskValidationError {
    NAME_EMPTY,
    NO_DAYS,
    END_NOT_AFTER_START
}

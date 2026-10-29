package com.example.dailytaskmanager.uistate

import java.time.LocalTime

data class UserInfoUiState(
    val name: String = "",
    val startingHour: LocalTime = LocalTime.of(8, 0)
) {
    val canSave: Boolean get() = name.isNotBlank()
}

enum class UserInfoValidationError {
    NAME_EMPTY
}
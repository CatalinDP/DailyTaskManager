package com.example.dailytaskmanager.ui

import androidx.annotation.StringRes
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.model.Day
import com.example.dailytaskmanager.model.ErrorReason
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import com.example.dailytaskmanager.uistate.CreateTaskValidationError
import com.example.dailytaskmanager.uistate.UserInfoValidationError

@StringRes
fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.theme_system
    ThemeMode.LIGHT -> R.string.theme_light
    ThemeMode.DARK -> R.string.theme_dark
}

@StringRes
fun TempUnit.labelRes(): Int = when (this) {
    TempUnit.CELSIUS -> R.string.temp_unit_celsius
    TempUnit.FAHRENHEIT -> R.string.temp_unit_fahrenheit
}

@StringRes
fun Day.labelRes(): Int = when (this) {
    Day.MONDAY -> R.string.day_monday
    Day.TUESDAY -> R.string.day_tuesday
    Day.WEDNESDAY -> R.string.day_wednesday
    Day.THURSDAY -> R.string.day_thursday
    Day.FRIDAY -> R.string.day_friday
    Day.SATURDAY -> R.string.day_saturday
    Day.SUNDAY -> R.string.day_sunday
}

@StringRes
fun ErrorReason.messageRes(): Int = when (this) {
    ErrorReason.NETWORK -> R.string.weather_error_network
    ErrorReason.SERVER -> R.string.weather_error_server
    ErrorReason.LOCATION_DENIED -> R.string.weather_error_location_denied
    ErrorReason.LOCATION_UNAVAILABLE -> R.string.weather_error_location_unavailable
}

@StringRes
fun UserInfoValidationError.messageRes(): Int = when (this) {
    UserInfoValidationError.NAME_EMPTY -> R.string.error_name_empty
}

@StringRes
fun CreateTaskValidationError.messageRes(): Int = when (this) {
    CreateTaskValidationError.NAME_EMPTY -> R.string.create_task_error_name_empty
    CreateTaskValidationError.NO_DAYS -> R.string.create_task_error_no_days
    CreateTaskValidationError.END_NOT_AFTER_START -> R.string.create_task_error_time_range
}

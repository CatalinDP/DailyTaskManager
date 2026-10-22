package com.example.dailytaskmanager.model

import com.example.dailytaskmanager.model.TempUnit.CELSIUS
import com.example.dailytaskmanager.model.TempUnit.FAHRENHEIT
import kotlin.math.roundToInt

enum class TempUnit { CELSIUS, FAHRENHEIT }

fun Double.toDisplay(unit: TempUnit): Int = when (unit) {
    CELSIUS -> roundToInt()
    FAHRENHEIT -> (this * 9 / 5 + 32).roundToInt()
}
package com.example.dailytaskmanager.model

import java.time.LocalTime

data class StandUpConfig(
    val interval: StandUpInterval = StandUpInterval.ZERO,
    val start: LocalTime = LocalTime.of(9, 0),
    val end: LocalTime = LocalTime.of(21, 0)
) {
    val enabled: Boolean get() = interval != StandUpInterval.ZERO
}
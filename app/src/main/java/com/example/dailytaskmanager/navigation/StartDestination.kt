package com.example.dailytaskmanager.navigation

sealed interface StartDestination {
    data object Onboarding : StartDestination
    data object Home : StartDestination
}

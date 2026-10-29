package com.example.dailytaskmanager.uistate

import com.example.dailytaskmanager.model.ThemeMode
import com.example.dailytaskmanager.navigation.StartDestination

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Ready(val theme: ThemeMode, val start: StartDestination) : MainUiState
}
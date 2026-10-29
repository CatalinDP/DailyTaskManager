package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.navigation.StartDestination
import com.example.dailytaskmanager.repository.SettingsRepository
import com.example.dailytaskmanager.repository.UserRepository
import com.example.dailytaskmanager.uistate.MainUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    userRepository: UserRepository,
    settingsRepository: SettingsRepository
): ViewModel() {
    val uiState: StateFlow<MainUiState> = combine(
        settingsRepository.theme,
        userRepository.user
    ) { theme, user ->
        MainUiState.Ready(
            theme,
            if (user == null) StartDestination.Onboarding else StartDestination.Home
            )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainUiState.Loading)
}
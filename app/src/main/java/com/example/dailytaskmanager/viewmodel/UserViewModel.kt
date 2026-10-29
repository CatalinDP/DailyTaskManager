package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.model.User
import com.example.dailytaskmanager.repository.UserRepository
import com.example.dailytaskmanager.uistate.UserInfoUiState
import com.example.dailytaskmanager.uistate.UserInfoValidationError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class UserViewModel(private val repository: UserRepository): ViewModel() {
    private val _uiState = MutableStateFlow(UserInfoUiState())
    val uiState: StateFlow<UserInfoUiState> = _uiState.asStateFlow()

    val userName = repository.user.map { it?.name.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name) }
    fun onStartingHourChange(startingHour: LocalTime) = _uiState.update { it.copy(startingHour = startingHour) }

    fun onSaveUserInfo(
        onSave: () -> Unit,
        onError: (UserInfoValidationError) -> Unit
    ) {
        val state = _uiState.value
        if (!state.canSave) {
            onError(UserInfoValidationError.NAME_EMPTY)
            return
        }
        viewModelScope.launch {
            repository.upsertUser(
                User(
                    name = state.name.trim(),
                    startingTime = state.startingHour
                )
            )
            _uiState.value = UserInfoUiState()
            onSave()
        }
    }
}

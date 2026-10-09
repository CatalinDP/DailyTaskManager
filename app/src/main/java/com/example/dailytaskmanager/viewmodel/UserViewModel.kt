package com.example.dailytaskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskmanager.dao.UserDao
import com.example.dailytaskmanager.model.User
import com.example.dailytaskmanager.uiState.UserInfoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime


class UserViewModel(private val userDao: UserDao): ViewModel() {
    private val _uiState = MutableStateFlow(UserInfoUiState())
    var uiState: StateFlow<UserInfoUiState> = _uiState.asStateFlow()

    private val _userName = MutableStateFlow("")
    private val _userStartingHour = MutableStateFlow(LocalTime.now())
    var userName: StateFlow<String> = _userName.asStateFlow()
    var userStartingHour = _userStartingHour.asStateFlow()

    init {
        viewModelScope.launch {
            _userName.value = userDao.getUserName() ?: ""
            _userStartingHour.value = userDao.getUserStartingTime() ?: LocalTime.now()
        }
    }

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name) }
    fun onStartingHourChange(startingHour: LocalTime) = _uiState.update { it.copy(startingHour = startingHour) }

    fun onSaveUserInfo(
        onSave: () -> Unit,
        onError: (String) -> Unit
    ) {
        val state = _uiState.value
        if (!state.canSave) {
            onError("Name cannot be empty")
            return
        }
        viewModelScope.launch {
            userDao.insertUser(
                User(
                    name = state.name.trim(),
                    startingTime = state.startingHour
                )
            )
            _uiState.value = UserInfoUiState()
            onSave()
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            val user = userDao.getUser()
            if (user != null) {
                userDao.deleteUser(user)
            }
        }
    }

}
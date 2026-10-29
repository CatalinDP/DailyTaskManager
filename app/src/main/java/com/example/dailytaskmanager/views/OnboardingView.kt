package com.example.dailytaskmanager.views

import com.example.dailytaskmanager.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.components.TimeInputComponent
import com.example.dailytaskmanager.ui.messageRes
import com.example.dailytaskmanager.uistate.UserInfoValidationError
import com.example.dailytaskmanager.utils.getFormattedCurrentDate
import com.example.dailytaskmanager.viewmodel.UserViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingView(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    userViewModel: UserViewModel = koinViewModel(),
) {
    val scrollState = rememberScrollState()
    var error by remember { mutableStateOf<UserInfoValidationError?>(null) }
    val state by userViewModel.uiState.collectAsStateWithLifecycle()
    val startTimeState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)

    Scaffold(modifier =
        Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = stringResource(
                            R.string.onboarding_title,
                            getFormattedCurrentDate()
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        }
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp)
                    .imePadding()
                    .verticalScroll(scrollState)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.starter_icon),
                        contentDescription = stringResource(R.string.onboarding_image_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(144.dp)
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.onboarding_welcome),
                        fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = stringResource(R.string.onboarding_tagline),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.onboarding_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column {
                    TimeInputComponent(
                        startTimeState = startTimeState,
                        startText = stringResource(R.string.onboarding_start_time_question),
                    )
                    error?.let {
                        Text(
                            text = stringResource(it.messageRes()),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_enter_name),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = userViewModel::onNameChange,
                        label = { Text(stringResource(R.string.onboarding_name_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            errorTextColor = MaterialTheme.colorScheme.error
                        ),
                        maxLines = 1
                    )
                }
                FilledTonalButton(
                    onClick = {
                        userViewModel.onStartingHourChange(LocalTime.of(startTimeState.hour, startTimeState.minute))
                        error = null
                        userViewModel.onSaveUserInfo(
                            onSave = onNavigateToHome,
                            onError = { error = it }
                        )
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_create_routine),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

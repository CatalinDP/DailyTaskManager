package com.example.dailytaskmanager.views

import android.graphics.drawable.Icon
import android.media.Image
import android.os.Build
import android.text.style.IconMarginSpan
import android.util.Log
import android.widget.Button
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import com.example.dailytaskmanager.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.components.TimeInputComponent
import com.example.dailytaskmanager.model.User
import com.example.dailytaskmanager.ui.theme.DailyTaskManagerTheme
import com.example.dailytaskmanager.utils.getFormattedCurrentDate
import com.example.dailytaskmanager.viewmodel.UserViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartView(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    userViewModel: UserViewModel = koinViewModel(),
) {
    val scrollState = rememberScrollState()
    var timeErrorState by remember { mutableStateOf(false) }
    var timeErrorText by remember { mutableStateOf("")}
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
                        text = "Daily Task Manager - ${getFormattedCurrentDate()}",
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
                        contentDescription = "Initial image for the app, a coffee and the sun behind",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(144.dp)
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Bienvenido a Daily Task Manager",
                        fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = "Tu día, con buen ritmo.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Organiza recordatorios sencillos, celebra tu progeso y deja espacio para lo importante",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column {
                    TimeInputComponent(
                        startTimeState = startTimeState,
                        startText = "¿A qué hora empieza tu día?",
                    )
                    if (timeErrorState) {
                        Text(
                            text = timeErrorText,
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
                        text = "Introduce tu nombre",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = userViewModel::onNameChange,
                        label = { Text("Nombre") },
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
                        userViewModel.onSaveUserInfo(
                            onSave = {
                                onNavigateToHome()
                            }, onError = {

                            }
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
                        text = "Crear mi rutina",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}


@Preview(
    showSystemUi = true
    )
@Composable
fun StartViewPreview() {
    DailyTaskManagerTheme() {
        StartView(
            modifier = Modifier.padding(4.dp),
            onNavigateToHome = {}
        )
    }
}

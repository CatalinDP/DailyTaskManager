package com.example.dailytaskmanager.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.components.AppTopBar
import com.example.dailytaskmanager.components.BigDoubleLineSection
import com.example.dailytaskmanager.components.OptionDialog
import com.example.dailytaskmanager.components.SingleLineSettingButtonSection
import com.example.dailytaskmanager.components.TextFieldDialog
import com.example.dailytaskmanager.components.TimeInputDialog
import com.example.dailytaskmanager.model.StandUpInterval
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import com.example.dailytaskmanager.ui.labelRes
import com.example.dailytaskmanager.utils.canScheduleExactAlarms
import com.example.dailytaskmanager.utils.isSystemLocationEnabled
import com.example.dailytaskmanager.utils.openAppSettings
import com.example.dailytaskmanager.utils.openExactAlarmSettings
import com.example.dailytaskmanager.utils.openNotificationSettings
import com.example.dailytaskmanager.utils.sendFeedback
import com.example.dailytaskmanager.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsView(
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

    var notificationsOn by remember { mutableStateOf(false) }
    var exactAlarmsOn by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }
    var showStandUpIntervalDialog by remember { mutableStateOf(false) }
    var showStandUpTimeDialog by remember { mutableStateOf(false) }
    var showUserNameDialog by remember { mutableStateOf(false) }
    var showUserStartingTimeDialog by remember { mutableStateOf(false) }

    val enabledText = stringResource(R.string.common_enabled)
    val disabledText = stringResource(R.string.common_disabled)
    val notificationsText = if (notificationsOn) enabledText else disabledText
    val exactAlarmsText = if (exactAlarmsOn) enabledText else disabledText
    val locationText = if (context.isSystemLocationEnabled()) enabledText else disabledText
    val theme = settingsViewModel.theme.collectAsStateWithLifecycle()
    val unit = settingsViewModel.unit.collectAsStateWithLifecycle()
    val showWeather = settingsViewModel.showWeather.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val standUpConfig = settingsViewModel.standUp.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val user = settingsViewModel.user.collectAsStateWithLifecycle()

    val config = standUpConfig.value
    val standUpStartState = key(config) { rememberTimePickerState(config.start.hour, config.start.minute, is24Hour = true) }
    val standUpEndState = key(config) { rememberTimePickerState(config.end.hour, config.end.minute, is24Hour = true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackText = stringResource(R.string.hint_setting_reset_confirmation)


    fun isTimeWindowValid(): Boolean {
        return LocalTime.of(standUpStartState.hour, standUpStartState.minute)
            .isBefore(LocalTime.of(standUpEndState.hour, standUpEndState.minute))
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        notificationsOn = NotificationManagerCompat.from(context).areNotificationsEnabled()
        exactAlarmsOn = context.canScheduleExactAlarms()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.settings_title),
                onBackButton = {}
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 88.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .verticalScroll(scrollState)
                .padding(start = 24.dp, end = 24.dp, bottom = 124.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionTitle(stringResource(R.string.settings_notifications_section))
            CardSettingContainer {
                BigDoubleLineSection(
                    title = stringResource(R.string.settings_notification_permission),
                    settingState = notificationsText
                ) {
                    context.openNotificationSettings()
                }
                SettingDivider()
                BigDoubleLineSection(
                    title = stringResource(R.string.settings_exact_alarms),
                    settingState = exactAlarmsText
                ) {
                    context.openExactAlarmSettings()
                }
            }
            SectionTitle(stringResource(R.string.settings_location_section))
            CardSettingContainer {
                BigDoubleLineSection(
                    title = stringResource(R.string.settings_location_permission),
                    settingState = locationText
                ) {
                    context.openAppSettings()
                }
            }
            SectionTitle(stringResource(R.string.settings_appearance_section))
            CardSettingContainer {
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.settings_theme),
                    settingState = stringResource(theme.value.labelRes()),
                ) { showThemeDialog = true }
            }
            SectionTitle(stringResource(R.string.settings_standup_section))
            CardSettingContainer {
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.settings_standup_title),
                    settingState = stringResource(standUpConfig.value.interval.labelRes),
                ) {
                    showStandUpIntervalDialog = true
                }
                SettingDivider()
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.standup_settings_window_selection),
                    settingState = "${standUpConfig.value.start} - ${standUpConfig.value.end}",
                    height = 68.dp
                ) {
                    showStandUpTimeDialog = true
                }
            }
            SectionTitle(stringResource(R.string.settings_weather_section))
            CardSettingContainer {
                ShowTimeSection(isChecked = showWeather.value, onCheckedChange = {
                    settingsViewModel.setShowWeather(it)
                })
                SettingDivider()
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.settings_units),
                    settingState = stringResource(unit.value.labelRes())
                ) { showUnitDialog = true }
            }
            SectionTitle(stringResource(R.string.settings_user_section_title))
            CardSettingContainer {
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.setting_change_user_name),
                    settingState = user.value?.name ?: ""
                ) {
                    showUserNameDialog = true
                }
                SettingDivider()
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.setting_change_starting_time),
                    settingState = user.value?.startingTime.toString()
                ) {
                    showUserStartingTimeDialog = true
                }
            }
            SectionTitle(stringResource(R.string.hint_setting_section_title))
            CardSettingContainer {
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.hint_setting_reset_hints),
                ) {
                    settingsViewModel.resetHints()
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = snackText,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
            SectionTitle(stringResource(R.string.settings_about_section))
            CardSettingContainer {
                VersionSection()
                SettingDivider()
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.settings_privacy_policy)
                ) { }
                SettingDivider()
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.settings_send_feedback)
                ) { context.sendFeedback() }
            }
            if (showThemeDialog) {
                OptionDialog(
                    title = stringResource(R.string.settings_theme),
                    options = ThemeMode.entries,
                    selected = theme.value,
                    label = { stringResource(it.labelRes()) },
                    onSelect = { settingsViewModel.setTheme(it) },
                    onDismiss = { showThemeDialog = false }
                )
            }
            if (showUnitDialog) {
                OptionDialog(
                    title = stringResource(R.string.settings_unit_dialog_title),
                    options = TempUnit.entries,
                    selected = unit.value,
                    label = { stringResource(it.labelRes()) },
                    onSelect = { settingsViewModel.setUnit(it) },
                    onDismiss = { showUnitDialog = false }
                )
            }
            if (showStandUpIntervalDialog) {
                OptionDialog(
                    title = stringResource(R.string.standup_title),
                    options = StandUpInterval.entries,
                    selected = standUpConfig.value.interval,
                    label = { stringResource(it.labelRes) },
                    onSelect = { settingsViewModel.setStandUpInterval(it) },
                    onDismiss = { showStandUpIntervalDialog = false }
                )
            }
            if (showUserNameDialog) {
                TextFieldDialog(
                    title = stringResource(R.string.setting_change_user_name),
                    label = stringResource(R.string.onboarding_name_label),
                    initialText = user.value?.name.orEmpty(),
                    onDismiss = { showUserNameDialog = false }
                ) {
                    settingsViewModel.changeUserName(it)
                    showUserNameDialog = false
                }
            }
            if (showUserStartingTimeDialog) {
                val startingTimeState = rememberTimePickerState(
                    initialHour = (user.value?.startingTime ?: LocalTime.of(8, 0)).hour,
                    initialMinute = (user.value?.startingTime ?: LocalTime.of(8, 0)).minute,
                    is24Hour = true
                )
                TimeInputDialog(
                    title = stringResource(R.string.setting_change_starting_time),
                    startState = startingTimeState,
                    startText = stringResource(R.string.settings_starting_time_dialog),
                    onConfirm = {
                        settingsViewModel.changeStartingTime(
                            LocalTime.of(startingTimeState.hour, startingTimeState.minute)
                        )
                        showUserStartingTimeDialog = false
                    },
                    onDismiss = { showUserStartingTimeDialog = false }
                )
            }
            if (showStandUpTimeDialog) {
                TimeInputDialog(
                    title = stringResource(R.string.settings_standup_title),
                    startState = standUpStartState,
                    startText = stringResource(R.string.standup_start_title),
                    endState = standUpEndState,
                    endText = stringResource(R.string.standup_end_title),
                    errorText = if (isTimeWindowValid()) null else stringResource(R.string.standup_time_error),
                    onConfirm = {
                        settingsViewModel.setStandUpWindow(
                            LocalTime.of(standUpStartState.hour, standUpStartState.minute),
                            LocalTime.of(standUpEndState.hour, standUpEndState.minute)
                        )
                        showStandUpTimeDialog = false
                    },
                    onDismiss = { showStandUpTimeDialog = false }
                )
            }
        }
    }
}

@Composable
fun CardSettingContainer(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        content()
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun SettingDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@Composable
fun ShowTimeSection(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_show_weather_home),
            style = MaterialTheme.typography.titleMedium,
        )
        Switch(
            onCheckedChange = onCheckedChange,
            checked = isChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                checkedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
fun VersionSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_version),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.settings_version_value),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}


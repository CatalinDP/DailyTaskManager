package com.example.dailytaskmanager.views

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytaskmanager.R
import com.example.dailytaskmanager.components.AppTopBar
import com.example.dailytaskmanager.components.OptionDialog
import com.example.dailytaskmanager.model.TempUnit
import com.example.dailytaskmanager.model.ThemeMode
import com.example.dailytaskmanager.ui.labelRes
import com.example.dailytaskmanager.utils.canScheduleExactAlarms
import com.example.dailytaskmanager.utils.getFormattedCurrentDate
import com.example.dailytaskmanager.utils.isSystemLocationEnabled
import com.example.dailytaskmanager.utils.openAppSettings
import com.example.dailytaskmanager.utils.openExactAlarmSettings
import com.example.dailytaskmanager.utils.openLocationSettings
import com.example.dailytaskmanager.utils.openNotificationSettings
import com.example.dailytaskmanager.utils.sendFeedback
import com.example.dailytaskmanager.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.util.Locale

@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsView(
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var notificationsOn by remember { mutableStateOf(false) }
    var exactAlarmsOn by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showUnityDialog by remember { mutableStateOf(false) }
    val enabledText = stringResource(R.string.common_enabled)
    val disabledText = stringResource(R.string.common_disabled)
    val notificationsText = if (notificationsOn) enabledText else disabledText
    val exactAlarmsText = if (exactAlarmsOn) enabledText else disabledText
    val locationText = if (context.isSystemLocationEnabled()) enabledText else disabledText
    val theme = settingsViewModel.theme.collectAsStateWithLifecycle()
    val unit = settingsViewModel.unit.collectAsStateWithLifecycle()
    val showWeather = settingsViewModel.showWeather.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()


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
                Divider()
                BigDoubleLineSection(
                    title = stringResource(R.string.settings_exact_alarms),
                    settingState = exactAlarmsText
                ) {
                    context.openExactAlarmSettings()
                }
                Divider()
                /*SingleLineSettingButtonSection( TODO Implement lead notifications
                    label = stringResource(R.string.settings_lead_time),
                    settingState = stringResource(R.string.settings_lead_time_exact),
                ) {}*/
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
            SectionTitle(stringResource(R.string.settings_weather_section))
            CardSettingContainer {
                ShowTimeSection(isChecked = showWeather.value, onCheckedChange = {
                    settingsViewModel.setShowWeather(it)
                })
                Divider()
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.settings_units),
                    settingState = stringResource(unit.value.labelRes())
                ) { showUnityDialog = true }
            }
            SectionTitle(stringResource(R.string.settings_about_section))
            CardSettingContainer {
                VersionSection()
                Divider()
                SingleLineSettingButtonSection(
                    label = stringResource(R.string.settings_privacy_policy)
                ) { }
                Divider()
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
            if (showUnityDialog) {
                OptionDialog(
                    title = stringResource(R.string.settings_unit_dialog_title),
                    options = TempUnit.entries,
                    selected = unit.value,
                    label = { stringResource(it.labelRes()) },
                    onSelect = { settingsViewModel.setUnit(it) },
                    onDismiss = { showUnityDialog = false }
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
fun Divider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@Composable
fun BigDoubleLineSection(
    title: String,
    settingState: String,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 4.dp, top = 12.dp, bottom = 0.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = settingState,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            TextButton(
                onClick = onClick,
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = stringResource(R.string.common_open_settings),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
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

@Composable
fun SingleLineSettingButtonSection(
    label: String,
    settingState: String = "",
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
        ) {
            if (!settingState.isEmpty()) {
                Text(
                    text = settingState,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowRight,
                contentDescription = stringResource(R.string.settings_open_setting_description)
            )
        }
    }
}
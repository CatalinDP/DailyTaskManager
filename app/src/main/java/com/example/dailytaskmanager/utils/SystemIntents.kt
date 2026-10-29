package com.example.dailytaskmanager.utils

import android.Manifest
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.example.dailytaskmanager.R
import timber.log.Timber

/** Starts [intent], telling the user instead of crashing when no app can handle it (OEM ROMs, no mail app). */
private fun Context.startActivitySafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.w(e, "No activity for ${intent.action}")
        Toast.makeText(this, R.string.error_no_app_to_open, Toast.LENGTH_SHORT).show()
    }
}

fun Context.openNotificationSettings() {
    startActivitySafely(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    })
}

fun Context.canScheduleExactAlarms(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

fun Context.openExactAlarmSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        startActivitySafely(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName"))
        )
    }
}

fun Context.sendFeedback() {
    startActivitySafely(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf("cdpstudioWebMobile@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_feedback_subject))
    })
}

fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

fun Context.isSystemLocationEnabled(): Boolean {
    val manager = getSystemService(LocationManager::class.java)
    return LocationManagerCompat.isLocationEnabled(manager)
}

fun Context.openAppSettings() {
    startActivitySafely(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    )
}

fun Context.openLocationSettings() {
    startActivitySafely(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}

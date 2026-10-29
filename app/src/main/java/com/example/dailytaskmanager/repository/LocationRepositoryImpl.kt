package com.example.dailytaskmanager.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import timber.log.Timber
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class LocationRepositoryImpl(private val context: Context): LocationRepository {
    private val client = LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    // Low power alone returns null without Wi-Fi/cell positioning (emulators, airplane mode),
    // so balanced accuracy is used, falling back to the last known fix
    override suspend fun currentLocation(): Location? = try {
        client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
            ?: client.lastLocation.await()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.w(e, "Location failed")
        null
    }

}
package com.example.dailytaskmanager.repository

import android.location.Location

interface LocationRepository {
    suspend fun currentLocation(): Location?
}
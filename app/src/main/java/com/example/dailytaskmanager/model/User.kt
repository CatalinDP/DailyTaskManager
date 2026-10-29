package com.example.dailytaskmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "users")
data class User(
    // Single-user app: always id 1, so upsert replaces the row instead of adding one
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val name: String? = null,
    val startingTime: LocalTime? = null
)

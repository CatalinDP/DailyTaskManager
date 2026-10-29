package com.example.dailytaskmanager.database

import androidx.room.migration.Migration

/**
 * Manual migrations, registered in [AppDatabase].
 *
 * Add a migration in the same change that bumps the database version and its entities,
 * and commit the exported schema (`app/schemas/.../<version>.json`) with it.
 * Prefer `@Database(autoMigrations = [...])` for additive changes such as new nullable columns.
 */
val ALL_MIGRATIONS: Array<Migration> = emptyArray()

package com.example.dailytaskmanager

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dailytaskmanager.database.ALL_MIGRATIONS
import com.example.dailytaskmanager.database.AppDatabase
import com.example.dailytaskmanager.model.Day
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

/**
 * Guards the data of users who already have the app installed.
 *
 * When the schema changes: bump the version, add the migration to [ALL_MIGRATIONS],
 * commit the new `app/schemas/.../<version>.json` and add a `migrateXToY` test here
 * using `helper.runMigrationsAndValidate(TEST_DB, Y, true, *ALL_MIGRATIONS)`.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    /**
     * A database created from the exported v7 schema must open with the current code.
     * Fails if the entities changed without bumping the version (Room's identity hash
     * check), or if the type converters no longer read the stored format.
     */
    @Test
    fun existingV7Database_opensWithCurrentCode_andKeepsData() {
        helper.createDatabase(TEST_DB, 7).use { db ->
            db.execSQL(
                "INSERT INTO tasks (id, name, startingTime, endingTime, repetitionDays, createdAt) " +
                        "VALUES (1, 'Read', '22:00', '22:30', 'MONDAY,FRIDAY', '2026-10-01')"
            )
            db.execSQL("INSERT INTO users (id, name, startingTime) VALUES (1, 'Ana', '08:00')")
            db.execSQL("INSERT INTO task_completion (taskId, date) VALUES (1, '2026-10-02')")
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB)
            .addMigrations(*ALL_MIGRATIONS)
            .build()

        try {
            runBlocking {
                val task = database.taskDao().getTaskById(1)
                assertNotNull(task)
                assertEquals("Read", task!!.name)
                assertEquals(LocalTime.of(22, 0), task.startingTime)
                assertEquals(LocalTime.of(22, 30), task.endingTime)
                assertEquals(listOf(Day.MONDAY, Day.FRIDAY), task.repetitionDays)
                assertEquals(LocalDate.of(2026, 10, 1), task.createdAt)

                val user = database.userDao().observeUser().first()
                assertEquals("Ana", user?.name)
                assertEquals(LocalTime.of(8, 0), user?.startingTime)

                assertTrue(database.taskDao().isCompleted(1, LocalDate.of(2026, 10, 2)))
            }
        } finally {
            database.close()
        }
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}

package com.example.dailytaskmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dailytaskmanager.model.Converters
import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.dao.TaskDao
import com.example.dailytaskmanager.dao.UserDao
import com.example.dailytaskmanager.model.User

@Database(entities = [ Task::class, User::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_tasks.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}
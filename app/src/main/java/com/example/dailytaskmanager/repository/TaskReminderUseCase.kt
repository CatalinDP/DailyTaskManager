package com.example.dailytaskmanager.repository

import com.example.dailytaskmanager.model.Task
import com.example.dailytaskmanager.notifications.TaskAlarmScheduler

class TaskReminderUseCase(
    private val taskRepository: TaskRepository,
    private val scheduler: TaskAlarmScheduler
) {

    suspend fun insertTask(task: Task) {
        val id = taskRepository.insertTask(task)
        scheduler.schedule(task.copy(id = id))
    }

    suspend fun updateTask(task: Task) {
        taskRepository.updateTask(task)
        scheduler.cancel(task.id)
        scheduler.schedule(task)
    }

    suspend fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
        scheduler.cancel(task.id)
    }
}
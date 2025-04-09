package org.taskflow.app.data.repository

import org.taskflow.app.data.model.Task
import org.taskflow.app.data.model.User
import org.taskflow.app.util.UiState

interface TaskRepository {
    fun addTask(task: Task, callback: (UiState<Pair<Task, String>>) -> Unit)
    fun updateTask(task: Task, callback: (UiState<Pair<Task, String>>) -> Unit)
    fun deleteTask(task: Task, callback: (UiState<Pair<Task, String>>) -> Unit)
    fun getTask(taskId: String, callback: (UiState<Pair<Task, String>>) -> Unit)
    fun getTasks(user: User?, callback: (UiState<List<Task>>) -> Unit)
    fun storeTasks(tasks: List<Task>, callback: (UiState<String>) -> Unit)
}

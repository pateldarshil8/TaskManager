package org.taskflow.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.taskflow.app.data.model.Task
import org.taskflow.app.data.model.User
import org.taskflow.app.data.repository.TaskRepository
import org.taskflow.app.util.UiState
import javax.inject.Inject

@HiltViewModel
class TaskManagerViewModel @Inject constructor(
    private val taskRepo: TaskRepository
) : ViewModel() {

    private val _taskCreation = MutableLiveData<UiState<Pair<Task, String>>>()
    val taskCreation: LiveData<UiState<Pair<Task, String>>> get() = _taskCreation

    private val _taskUpdate = MutableLiveData<UiState<Pair<Task, String>>>()
    val taskUpdate: LiveData<UiState<Pair<Task, String>>> get() = _taskUpdate

    private val _taskToggleDone = MutableLiveData<UiState<Pair<Task, String>>>()
    val taskToggleDone: LiveData<UiState<Pair<Task, String>>> get() = _taskToggleDone

    private val _taskDeletion = MutableLiveData<UiState<Pair<Task, String>>>()
    val taskDeletion: LiveData<UiState<Pair<Task, String>>> get() = _taskDeletion

    private val _taskStorage = MutableLiveData<UiState<String>>()
    val taskStorage: LiveData<UiState<String>> get() = _taskStorage

    private val _fetchedTask = MutableLiveData<UiState<Pair<Task, String>>>()
    val fetchedTask: LiveData<UiState<Pair<Task, String>>> get() = _fetchedTask

    private val _taskFetchList = MutableLiveData<UiState<List<Task>>>()
    val taskFetchList: MutableLiveData<UiState<List<Task>>> get() = _taskFetchList

    private val _allTasks = MutableLiveData<UiState<List<Task>>>()
    val allTasks: LiveData<UiState<List<Task>>> get() = _allTasks

    fun createTask(task: Task) {
        _taskCreation.value = UiState.Loading
        taskRepo.addTask(task) { result ->
            _taskCreation.value = result
        }
    }

    fun modifyTask(task: Task) {
        _taskUpdate.value = UiState.Loading
        taskRepo.updateTask(task) { result ->
            _taskUpdate.value = result
        }
    }

    fun toggleTaskCompletion(task: Task) {
        _taskToggleDone.value = UiState.Loading
        task.completed = !task.completed
        taskRepo.updateTask(task) { result ->
            _taskToggleDone.value = result
        }
    }

    fun removeTask(task: Task) {
        _taskDeletion.value = UiState.Loading
        taskRepo.deleteTask(task) { result ->
            _taskDeletion.value = result
        }
    }

    fun fetchTaskById(taskId: String) {
        _fetchedTask.value = UiState.Loading
        taskRepo.getTask(taskId) { result ->
            _fetchedTask.value = result
        }
    }

    fun fetchUserTasks(user: User?) {
        _allTasks.value = UiState.Loading
        taskRepo.getTasks(user) { result ->
            _allTasks.value = result
        }
    }

    fun saveTaskList(tasks: List<Task>) {
        _taskStorage.value = UiState.Loading
        taskRepo.storeTasks(tasks) { result ->
            _taskStorage.value = result
        }
    }
}

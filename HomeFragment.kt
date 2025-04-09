package org.taskflow.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import org.taskflow.app.R
import org.taskflow.app.data.model.Task
import org.taskflow.app.databinding.FragmentHomeBinding
import org.taskflow.app.ui.auth.AuthViewModel
import org.taskflow.app.util.UiState
import org.taskflow.app.util.hide
import org.taskflow.app.util.show
import org.taskflow.app.util.snackbar

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var ui: FragmentHomeBinding
    private val taskVM: TaskViewModel by viewModels()
    private val sessionVM: AuthViewModel by viewModels()

    private val taskAdapter by lazy {
        TaskAdapter(
            donePressed = { markAsDone(it) },
            deletePressed = { removeTask(it) },
            editPressed = { editTask(it) }
        )
    }

    private var allTasks = mutableListOf<Task>()
    private var pendingTasks = mutableListOf<Task>()
    private var finishedTasks = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::ui.isInitialized) {
            return ui.root
        }
        ui = FragmentHomeBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.addTaskButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, TaskEditorFragment())
                .commit()
        }

        ui.taskList.layoutManager = LinearLayoutManager(requireContext())
        ui.taskList.adapter = taskAdapter

        sessionVM.getSession {
            taskVM.getTasks(it)
        }

        observeTasks()
        handleTabChanges()
    }

    private fun observeTasks() {
        taskVM.tasks.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> ui.progressBar.show()
                is UiState.Success -> {
                    ui.progressBar.hide()
                    allTasks = state.data.toMutableList()
                    pendingTasks = allTasks.filter { !it.completed }.toMutableList()
                    taskAdapter.updateList(pendingTasks)
                }
                is UiState.Failure -> {
                    ui.progressBar.hide()
                    snackbar("Error loading tasks")
                }
                else -> ui.progressBar.hide()
            }
        }
    }

    private fun handleTabChanges() {
        ui.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            pendingTasks = allTasks.filter { task -> !task.completed }.toMutableList()
                            taskAdapter.updateList(pendingTasks)
                        }
                        1 -> {
                            finishedTasks = allTasks.filter { task -> task.completed }.toMutableList()
                            taskAdapter.updateList(finishedTasks)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun removeTask(task: Task) {
        taskVM.deleteTask(task)
        snackbar("Task deleted successfully!")
        refresh()
    }

    private fun markAsDone(task: Task) {
        taskVM.doneTask(task)
        snackbar("Toggled done status!")
        refresh()
    }

    private fun editTask(task: Task) {
        val editorSheet = TaskEditorFragment(task)
        editorSheet.setOnDismissListener {
            if (it) {
                sessionVM.getSession { user -> taskVM.getTasks(user) }
            }
        }
        editorSheet.show(childFragmentManager, "edit_task")
    }

    private fun refresh() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, DashboardFragment())
            .commit()
    }
}

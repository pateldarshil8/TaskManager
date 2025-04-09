package org.taskflow.app.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import org.taskflow.app.R
import org.taskflow.app.data.model.Task
import org.taskflow.app.databinding.FragmentAddTaskBinding
import org.taskflow.app.ui.auth.AuthViewModel
import org.taskflow.app.util.UiState
import org.taskflow.app.util.snackbar

@AndroidEntryPoint
class TaskEditorFragment(private val incomingTask: Task? = null) : BottomSheetDialogFragment() {

    private lateinit var ui: FragmentAddTaskBinding
    private var onDismissCallback: ((Boolean) -> Unit)? = null
    private var isSaveSuccessful: Boolean = false
    private val taskViewModel: TaskViewModel by viewModels()
    private val userViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui = FragmentAddTaskBinding.bind(view)

        incomingTask?.let {
            ui.taskName.setText(it.title)
            ui.taskDescription.setText(it.description)
            ui.dueDateDropdown.setText(it.dueDate)
            ui.priorityDropdown.setText(it.priority)
            ui.locationTextField.setText(it.location)
        }

        setupPriorityDropdown()
        setupDatePicker()

        ui.addTaskButton.setOnClickListener {
            val taskData = collectInput()
            if (taskData.id.isBlank()) {
                taskViewModel.addTask(taskData)
            } else {
                taskViewModel.updateTask(taskData)
            }

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, HomeFragment())
                ?.commit()
        }

        observeTaskEvents()
    }

    private fun setupPriorityDropdown() {
        val priorities = resources.getStringArray(R.array.priority)
        val adapter = activity?.let {
            ArrayAdapter(it, R.layout.dropdown_item, priorities)
        }
        ui.priorityDropdown.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .build()

        ui.dueDateDropdown.setOnClickListener {
            picker.show(childFragmentManager, "DATE_PICKER")
        }

        picker.addOnPositiveButtonClickListener {
            ui.dueDateDropdown.setText(picker.headerText)
        }
    }

    private fun collectInput(): Task {
        return Task(
            title = ui.taskName.text.toString(),
            description = ui.taskDescription.text.toString(),
            dueDate = ui.dueDateDropdown.text.toString(),
            priority = ui.priorityDropdown.text.toString(),
            location = ui.locationTextField.text.toString()
        )
    }

    private fun observeTaskEvents() {
        taskViewModel.addTask.observe(viewLifecycleOwner) { state ->
            handleTaskState(state, "Task added successfully")
        }
        taskViewModel.updateTask.observe(viewLifecycleOwner) { state ->
            handleTaskState(state, "Task updated successfully")
        }
    }

    private fun handleTaskState(state: UiState<String>, successMsg: String) {
        when (state) {
            is UiState.Loading -> ui.addTaskButton.isEnabled = false
            is UiState.Success -> {
                isSaveSuccessful = true
                snackbar(successMsg)
                onDismissCallback?.invoke(true)
                dismiss()
            }
            is UiState.Failure -> {
                snackbar("Save failed, please try again")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke(isSaveSuccessful)
    }

    fun setOnDismissListener(callback: ((Boolean) -> Unit)?) {
        onDismissCallback = callback
    }
}

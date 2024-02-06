package com.example.stepsync.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stepsync.R
import com.example.stepsync.adapters.RvStepsAdapter
import com.example.stepsync.databinding.FragmentAddStepBinding
import com.example.stepsync.databinding.FragmentNewProjectBinding
import com.example.stepsync.roomUtils.Priority
import com.example.stepsync.roomUtils.Project
import com.example.stepsync.roomUtils.Status
import com.example.stepsync.roomUtils.Step
import com.example.stepsync.utils.Resource
import com.example.stepsync.viewModel.ProjectsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class NewProjectFragment : BottomSheetDialogFragment(R.layout.fragment_new_project) {
    private lateinit var binding: FragmentNewProjectBinding
    private var endDate: Date? = null
    private val bindingNewStep by lazy {
        FragmentAddStepBinding.inflate(layoutInflater)
    }
    private val stepList: MutableList<Step> = mutableListOf()
    private val rvStepsAdapter by lazy {
        RvStepsAdapter()
    }
    private var priority = Priority.MEDIUM

    @Inject
    lateinit var viewModel: ProjectsViewModel

    var addProjectToUI: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().setTheme(R.style.Theme_StepSync)
        binding = FragmentNewProjectBinding.inflate(layoutInflater, container, false)


        //to observe steps
        lifecycleScope.launchWhenStarted {
            viewModel.steps.collect {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            if (it.name == "Start")
                                stepList.add(0,it)
                            else
                                stepList.add(it)
                        }

                        rvStepsAdapter.differ.submitList(stepList)
                        rvStepsAdapter.notifyDataSetChanged()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvSteps()


        //to handle priority input
        binding.tvPriorityValue.setOnClickListener {
            showPriorityMenu()
        }

        //to set todays date to project
        binding.tvSelectStartDate.text = getTodaysDate().toLocaleString()

        //to set end date
        binding.tvSelectEndDate.setOnClickListener {
            showDatePicker()
        }

        //to add steps
        binding.buttonAddSteps.setOnClickListener {
            startAddStepDialogue()
        }

        //to handle cancel
        binding.buttonCancelProject.setOnClickListener {
            dismiss()
        }

        // To save the project to database
        binding.buttonSaveProject.setOnClickListener {
            saveProjectToDatabase()
        }

    }

    private fun saveProjectToDatabase() {
        if (binding.edProjectName.text.isNullOrEmpty() || binding.edProjectName.text.isNullOrBlank())
            binding.edProjectName.error = "Enter project name"
        else {

            val projectName = binding.edProjectName.text.toString()
            val projectDescription = binding.edProjectDescription.text.toString()
            val startDate = getTodaysDate()
            val stepIndexList = mutableListOf<Int>()

            lifecycleScope.launch {
                // add Start and complete step
                viewModel.addStepToDB(Step("Start",Status.NOT_STARTED))
                delay(100)
                viewModel.addStepToDB(Step("Complete",Status.NOT_STARTED))

                // time to let the database update
                delay(600)

                stepList.forEach { it ->
                    stepIndexList.add(it.id)
                }

                val project = Project(
                    projectName,
                    projectDescription,
                    startDate,
                    endDate,
                    Status.NOT_STARTED,
                    stepIndexList,
                    priority
                )

                viewModel.addProjectToDB(project)
                addProjectToUI()
                dismiss()
            }

        }
    }


    private fun setupRvSteps() {
        binding.apply {
            rvSteps.adapter = rvStepsAdapter
            rvStepsAdapter.differ.submitList(stepList)
            rvSteps.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun startAddStepDialogue() {
        // To ensure that the dialog is not in use
        val parent = bindingNewStep.root.parent as? ViewGroup
        parent?.removeView(bindingNewStep.root)

        // To clear the previously entered Name
        bindingNewStep.edStepName.text.clear()

        val addStepDialogue = AlertDialog.Builder(requireContext())
            .setView(bindingNewStep.root)
            .setPositiveButton(
                "Add Step"
            ) { dialog, _ -> addStepToDatabase() }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog?.dismiss() }
            .create()


        addStepDialogue.show()

    }

    private fun addStepToDatabase() {
        val stepName = bindingNewStep.edStepName.text.toString()
        val step = Step(stepName, Status.NOT_STARTED)
        viewModel.addStepToDB(step)
    }

    private fun showDatePicker() {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.customDatePickerTheme,
            { _, year, month, dayOfMonth -> onDateSelected(year, month, dayOfMonth) },
            now.year,
            now.monthValue - 1,
            now.dayOfMonth
        )


        datePickerDialog.show()

    }

    private fun onDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val localDate = LocalDate.of(year, month + 1, dayOfMonth)
        val instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        val selectedDate = Date.from(instant)

        if (selectedDate < getTodaysDate() || selectedDate == getTodaysDate()) {
            Toast.makeText(
                requireContext(),
                "Project Deadline can only be now onwards",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            binding.tvSelectEndDate.text = selectedDate.toLocaleString()
            endDate = selectedDate
        }
    }


    private fun showPriorityMenu() {
        var priorityPopup = PopupMenu(requireContext(), binding.tvPriorityValue)
        priorityPopup.inflate(R.menu.priority_menu)

        priorityPopup.show()

        priorityPopup.setOnMenuItemClickListener { it ->
            handleOnPriorityMenuItemClick(it)
        }
    }

    private fun handleOnPriorityMenuItemClick(it: MenuItem): Boolean {
        when (it.itemId) {
            R.id.veryLow -> {
                binding.tvPriorityValue.text = "Very Low"
                binding.dotPriority.setBackgroundResource(R.drawable.dot_very_low_)
                priority = Priority.VERYLOW
                return true
            }

            R.id.low -> {
                binding.tvPriorityValue.text = "Low"
                binding.dotPriority.setBackgroundResource(R.drawable.dot_low_)
                priority = Priority.LOW
                return true
            }

            R.id.medium -> {
                binding.tvPriorityValue.text = "Medium"
                binding.dotPriority.setBackgroundResource(R.drawable.dot_medium_)
                priority = Priority.MEDIUM
                return true
            }

            R.id.high -> {
                binding.tvPriorityValue.text = "High"
                binding.dotPriority.setBackgroundResource(R.drawable.dot_high_)
                priority = Priority.HIGH
                return true
            }

            R.id.veryHigh -> {
                binding.tvPriorityValue.text = "Very High"
                binding.dotPriority.setBackgroundResource(R.drawable.dot_very_high_)
                priority = Priority.VERYHIGH
                return true
            }

            else -> {
                return false
            }
        }
    }

    private fun getTodaysDate(): Date {
        val today = LocalDateTime.now()
        val todaysDate = Date.from(today.atZone(ZoneId.systemDefault()).toInstant())
        return todaysDate
    }

}
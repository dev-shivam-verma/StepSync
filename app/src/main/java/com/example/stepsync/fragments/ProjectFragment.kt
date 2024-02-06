package com.example.stepsync.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stepsync.R
import com.example.stepsync.adapters.RvStepsDisplayAdapter
import com.example.stepsync.databinding.FragmentProjectBinding
import com.example.stepsync.databinding.ItemStepBinding
import com.example.stepsync.roomUtils.Project
import com.example.stepsync.roomUtils.Status
import com.example.stepsync.roomUtils.Step
import com.example.stepsync.utils.Resource
import com.example.stepsync.viewModel.ProjectsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ProjectFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentProjectBinding
    private val project: Project? by lazy {
        arguments?.getParcelable("project")
    }
    private var stepList = mutableListOf<Step>()
    private val stepsDisplayAdapter by lazy {
        RvStepsDisplayAdapter()
    }

    @Inject
    lateinit var viewModel: ProjectsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectBinding.inflate(inflater, container, false)


        // To observe step list
        lifecycleScope.launchWhenStarted {
            viewModel.stepList.collect{
                when(it){
                    is Resource.Success -> {
                        it.data?.let {
                            stepList= it as MutableList<Step>
                            stepsDisplayAdapter.differ.submitList(it)
                            stepsDisplayAdapter.notifyDataSetChanged()
                        }
                    }
                    else -> {}
                }
            }
        }

        val flowProgress = viewModel.getProgress(project)

        // To observe update in progress
        lifecycleScope.launch {
            flowProgress.collect {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            val progressText = "%.2f".format(it) + "%"
                            binding.tvProgressMeasure.animateTo(progressText)
                        }
                    }

                    else -> {}
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // To change step status
        stepsDisplayAdapter.onStepClick = ::changeStepStatus

        binding.apply {

            setProjectDataToUi()

            edProjectNameDisplay.addTextChangedListener {
                showChangeNameButton()
            }

            buttonChangeName.setOnClickListener {
                changeProjectName()
                buttonChangeName.visibility = View.GONE
                edProjectNameDisplay.clearFocus()
            }

            edProjectDescriptionDisplay.addTextChangedListener {
                showChangeDescriptionButton()
            }

            buttonChangeDescription.setOnClickListener {
                changeProjectDesctiption()
                buttonChangeDescription.visibility = View.GONE
                edProjectDescriptionDisplay.clearFocus()
            }
        }

    }

    // runnable on project dismiss by another class
    var onWindowDismis: () -> Unit = {}
    override fun onPause() {
        super.onPause()
        onWindowDismis()
    }

    private fun changeStepStatus(step: Step, bindingStep: ItemStepBinding) {
        when (step.status) {
            Status.NOT_STARTED -> {
                step.status = Status.IN_PROGRESS
                bindingStep.root.setBackgroundResource(R.drawable.step_inprogress)
            }

            Status.IN_PROGRESS -> {
                step.status = Status.COMPLETED
                bindingStep.root.setBackgroundResource(R.drawable.step_completed)
            }

            Status.COMPLETED -> {
                step.status = Status.NOT_STARTED
                bindingStep.root.setBackgroundResource(R.drawable.step_not_started)
            }
        }

        viewModel.updateStep(step)
        val flowProgress = viewModel.getProgress(project)

        // To observe update in progress
        lifecycleScope.launch {
            flowProgress.collect {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            val progressText = "%.2f".format(it) + "%"
                            binding.tvProgressMeasure.animateTo(progressText)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setProjectDataToUi() {
        binding.apply {
            edProjectNameDisplay.setText(project?.name)
            edProjectDescriptionDisplay.setText(project?.description)

            rvStepsDisplay.adapter = stepsDisplayAdapter
            rvStepsDisplay.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            project?.let {
                viewModel.getAllStepsFromProject(it)
            }
        }
    }



    private fun changeProjectDesctiption() {
        project?.description = binding.edProjectDescriptionDisplay.text.toString()
        viewModel.updateProject(project)

        Toast.makeText(requireContext(), "Description changed" , Toast.LENGTH_SHORT).show()
    }

    private fun showChangeDescriptionButton() {
        binding.edProjectDescriptionDisplay.requestFocus()
        binding.buttonChangeDescription.visibility = View.VISIBLE
    }

    private fun showChangeNameButton() {
        binding.edProjectNameDisplay.requestFocus()
        binding.buttonChangeName.visibility = View.VISIBLE
    }

    private fun changeProjectName() {
        project?.name = binding.edProjectNameDisplay.text.toString()
        viewModel.updateProject(project)

        Toast.makeText(requireContext(), "Name Changed", Toast.LENGTH_SHORT).show()
    }

}
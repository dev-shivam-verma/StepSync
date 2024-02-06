package com.example.stepsync.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepsync.roomUtils.DBProjectHolder
import com.example.stepsync.roomUtils.Project
import com.example.stepsync.roomUtils.Status
import com.example.stepsync.roomUtils.Step
import com.example.stepsync.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class ProjectsViewModel @Inject constructor(
    val db: DBProjectHolder
) : ViewModel() {

    private val _projects = MutableStateFlow<Resource<Project>>(Resource.Unspecified())
    val projects = _projects.asSharedFlow()

    private val _steps = MutableStateFlow<Resource<Step>>(Resource.Unspecified())
    val steps = _steps.asStateFlow()

    private val _projectList = MutableStateFlow<Resource<List<Project?>>>(Resource.Unspecified())
    val projectList = _projectList.asStateFlow()

    private val _stepList = MutableStateFlow<Resource<List<Step>>>(Resource.Unspecified())
    val stepList = _stepList.asStateFlow()


    fun addProjectToDB(project: Project) {
        viewModelScope.launch {
            db.getProjectDao().insertProject(project)
            _projects.emit(Resource.Success(project))
        }
    }

    fun addStepToDB(step: Step) {
        if (validateStep(step)) {
            viewModelScope.launch {
                db.getProjectDao().insertStep(step)

                val stepsInDb = db.getProjectDao().getStepsOrderedByIds()
                val stepInDb = stepsInDb[stepsInDb.size - 1]
                _steps.emit(Resource.Success(stepInDb))
            }

        } else
            viewModelScope.launch {
                _steps.emit(Resource.Error("Step must have a name"))
            }
    }

    fun getAllProjects() {
        viewModelScope.launch {
            _projectList.emit(Resource.Loading())

            val projects = db.getProjectDao().getAllProjects()
            projects?.let {
                _projectList.emit(Resource.Success(it))
            }
        }
    }

    fun getProjectByName(projectName: String) {
        viewModelScope.launch {
            _projectList.emit(Resource.Loading())

            val projects = db.getProjectDao().getProjectsByProjectName(projectName)
            projects?.let {
                _projectList.emit(Resource.Success(it))
            }
        }
    }

    private fun validateStep(step: Step): Boolean {
        return !(step.name.isNullOrBlank() || step.name.isNullOrEmpty())
    }

    fun updateStep(step: Step) {
        viewModelScope.launch {
            db.getProjectDao().updateStep(step)
        }
    }

    fun getAllStepsFromProject(project: Project) {
        viewModelScope.launch {
            val stepIds = project?.steps
            stepIds?.let {
                // To let database update
                delay(100)

                val steps = mutableListOf<Step>()

                // Gets all steps of the project from database
                stepIds.forEach {
                    val step = db.getProjectDao().getStepByStepId(it)
                    step?.let {
                        steps.add(it)
                    }
                }

                _stepList.emit(Resource.Success(steps))
            }
        }
    }


    fun getProgress(project: Project?): StateFlow<Resource<Float>> {
        val _flowProgress = MutableStateFlow<Resource<Float>>(Resource.Unspecified())

        viewModelScope.launch {
            val stepIds = project?.steps
            stepIds?.let {
                // To let database update
                delay(100)

                val steps = mutableListOf<Step>()

                // Gets all steps of the project from database
                stepIds.forEach {
                    val step = db.getProjectDao().getStepByStepId(it)
                    step?.let {
                        steps.add(it)
                    }
                }

                var progress = 0.0f

                // Calculates the progress
                steps.forEach {
                    if (it.status.equals(Status.IN_PROGRESS))
                        progress += (0.5f / steps.size) * 100.0f
                    else if (it.status.equals(Status.COMPLETED))
                        progress += (1.0f / steps.size) * 100.0f
                }

                _flowProgress.emit(Resource.Success(progress))
            }
        }

        return _flowProgress.asStateFlow()
    }

    fun deletePrject(project: Project) {
        viewModelScope.launch {
            db.getProjectDao().deleteProject(project)
        }
    }

    fun updateProject(project: Project?) {
        viewModelScope.launch {
            project?.let {
                db.getProjectDao().updateProject(it)
            }
        }
    }
}
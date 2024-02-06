package com.example.stepsync.fragments

import android.app.AlertDialog
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stepsync.R
import com.example.stepsync.adapters.RvProjectsAdapter
import com.example.stepsync.databinding.FragmentHomeBinding
import com.example.stepsync.roomUtils.Project
import com.example.stepsync.sharedpreferanceUtils.UserData
import com.example.stepsync.utils.Resource
import com.example.stepsync.viewModel.ProjectsViewModel
import com.smb.animatedtextview.AnimatedTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val rvProjectsAdapter by lazy {
        RvProjectsAdapter()
    }
    private val projectList = mutableListOf<Project>()

    @Inject
    lateinit var viewModel: ProjectsViewModel

    @Inject
    lateinit var userData: UserData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Observe project list
        lifecycleScope.launchWhenStarted {
            viewModel.projectList.collect {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        projectList.clear()
                        it.data?.let {
                            it.forEach {
                                it?.let {
                                    projectList.add(it)
                                }
                            }
                        }
                        rvProjectsAdapter.differ.submitList(projectList)


                        // To avoid reusing of views and display new data in recycler view
                        rvProjectsAdapter.notifyDataSetChanged()
                    }

                    is Resource.Error -> {
                        hideLoading()
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

        setupRvProjects()

        // setting up the project items listner
        rvProjectsAdapter.projectProgressLoader = ::setupProjectsProgress
        rvProjectsAdapter.onDeleteProject = ::onDeleteProject

        binding.apply {

            // To increase the click area
            llSearchBar.setOnClickListener {
                llSearchBar.isIconified = false
            }

            // To implement search action
            llSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.getProjectByName(it)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        viewModel.getProjectByName(it)
                    }
                    return true
                }
            })


            // To launch New Project Fragment
            buttonNewProject.setOnClickListener {
                val newProjectFragment = NewProjectFragment()
                newProjectFragment
                    .show(childFragmentManager, "NewProjectFragment")

                newProjectFragment.addProjectToUI = {
                    lifecycleScope.launch {
                        // to let database update and avoid race condition
                        delay(100)
                        viewModel.getAllProjects()
                    }
                }
            }


            // To handle on Project item click
            rvProjectsAdapter.onProjectClickListner = { project, position, view ->
                val bundle = Bundle()
                bundle.putParcelable("project", project)
                bundle.putInt("position", position)
                val projectFragment = ProjectFragment()
                projectFragment.setArguments(bundle)

                //to update progress
                projectFragment.onWindowDismis = {
                    project?.let {
                        setupProjectsProgress(project, view)
                    }
                }
                projectFragment.show(childFragmentManager, projectFragment.tag)
            }

            val userBitmap = userData.getUserPic()
            ivUserPicHome.setImageBitmap(userBitmap)
            ivUserPicHome.scaleType = ImageView.ScaleType.CENTER_CROP
        }

    }

    private fun onDeleteProject(project: Project) {
        val warningDialog = AlertDialog.Builder(requireContext())
            .setMessage("Delete Project?")
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.deletePrject(project)
                val newList = rvProjectsAdapter.differ.currentList.toMutableList()
                newList.remove(project)
                rvProjectsAdapter.differ.submitList(newList)
                rvProjectsAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Project deleted!!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .create()

        warningDialog.show()
    }

    private fun setupProjectsProgress(project: Project, view: AnimatedTextView) {
        val progressFlow = viewModel.getProgress(project)

        lifecycleScope.launch {
            progressFlow.collect {
                if (it is Resource.Success) {
                    it.data?.let {
                        val progressText = "%.2f".format(it) + "%"
                        view.animateTo(progressText)
                    }
                }
            }
        }
    }


    private fun setupRvProjects() {
        binding.rvProjects.adapter = rvProjectsAdapter
        binding.rvProjects.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getAllProjects()
    }

    private fun hideLoading() {
        binding.rvProjects.visibility = View.VISIBLE
        binding.pbProjects.visibility = View.GONE
    }

    private fun showLoading() {
        binding.rvProjects.visibility = View.GONE
        binding.pbProjects.visibility = View.VISIBLE
    }


}
package com.example.stepsync.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stepsync.R
import com.example.stepsync.databinding.ItemProjectBinding
import com.example.stepsync.databinding.ItemStepBinding
import com.example.stepsync.roomUtils.Priority
import com.example.stepsync.roomUtils.Project
import com.smb.animatedtextview.AnimatedTextView

class RvProjectsAdapter() : RecyclerView.Adapter<RvProjectsAdapter.RvProjectsViewHolder>() {


    var onProjectClickListner: (Project,Int,AnimatedTextView) -> Unit = {_,_,_->}
    var projectProgressLoader: (Project,AnimatedTextView) -> Unit = {_,_->}
    var onDeleteProject: (Project) -> Unit = {}
    inner class RvProjectsViewHolder(
        val binding: ItemProjectBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.apply {
                when(project.priority){
                    Priority.VERYLOW -> {dotPriorityProject.setBackgroundResource(R.drawable.dot_very_low_)}
                    Priority.LOW -> {dotPriorityProject.setBackgroundResource(R.drawable.dot_low_)}
                    Priority.MEDIUM -> {dotPriorityProject.setBackgroundResource(R.drawable.dot_medium_)}
                    Priority.HIGH -> {dotPriorityProject.setBackgroundResource(R.drawable.dot_high_)}
                    Priority.VERYHIGH -> {dotPriorityProject.setBackgroundResource(R.drawable.dot_very_high_)}
                }

                projectProgressLoader(project,tvProgressPrecentage)

                val startDateText = "Start Date: ${project.startDate.toLocaleString()}"

                tvProjectName.text = project.name
                tvStartDate.text = startDateText

                binding.root.setOnClickListener {
                    onProjectClickListner(project,adapterPosition,binding.tvProgressPrecentage)
                }

                binding.buttonDeleteProject.setOnClickListener{
                    onDeleteProject(project)
                }
            }
        }
    }


    private val differCallback = object : DiffUtil.ItemCallback<Project>(){
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvProjectsViewHolder {
        return RvProjectsViewHolder(
            ItemProjectBinding.inflate(
                /* inflater = */ LayoutInflater.from(parent.context),
                /* parent = */ parent,
                /* attachToParent = */ false
            )
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }



    override fun onBindViewHolder(holder: RvProjectsViewHolder, position: Int) {
        val project = differ.currentList[position]
        holder.bind(project)
    }

}
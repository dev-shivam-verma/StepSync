package com.example.stepsync.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stepsync.R
import com.example.stepsync.databinding.ItemStepBinding
import com.example.stepsync.roomUtils.Status
import com.example.stepsync.roomUtils.Step
import com.example.stepsync.viewModel.ProjectsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject



class RvStepsDisplayAdapter(): RecyclerView.Adapter<RvStepsDisplayAdapter.RvStepsViewHolder>() {


    var onStepClick: ((Step,ItemStepBinding) -> Unit) = {step,binding ->}
    inner class RvStepsViewHolder(val binding: ItemStepBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(step: Step) {
            binding.tvStepName.text = step.name
            binding.root.setOnClickListener {
                onStepClick(step,binding)
            }

            when (step.status){
                Status.NOT_STARTED -> {
                    binding.root.setBackgroundResource(R.drawable.step_not_started)
                }
                Status.IN_PROGRESS -> {
                    binding.root.setBackgroundResource(R.drawable.step_inprogress)
                }
                Status.COMPLETED -> {
                    binding.root.setBackgroundResource(R.drawable.step_completed)
                }
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Step>() {
        override fun areItemsTheSame(oldItem: Step, newItem: Step): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Step, newItem: Step): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvStepsViewHolder {
        return RvStepsViewHolder(ItemStepBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RvStepsViewHolder, position: Int) {
        val step = differ.currentList[position]
        holder.bind(step)
    }
}
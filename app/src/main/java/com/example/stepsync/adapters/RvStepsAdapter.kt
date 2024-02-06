package com.example.stepsync.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stepsync.databinding.ItemStepBinding
import com.example.stepsync.roomUtils.Step

class RvStepsAdapter(): RecyclerView.Adapter<RvStepsAdapter.RvStepsViewHolder>() {

    inner class RvStepsViewHolder(val binding: ItemStepBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(step: Step) {
            binding.tvStepName.text = step.name
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
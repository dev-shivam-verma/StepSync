package com.example.stepsync.fragments.welcomeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stepsync.R
import com.example.stepsync.databinding.FragmentWelcomeBinding

class WelcomeFragment: Fragment() {
    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {
            // Initial aniamation
            val animationLeftEntry = AnimationUtils.loadAnimation(requireContext(),R.anim.left_entry)
            val animationRightEntry = AnimationUtils.loadAnimation(requireContext(),R.anim.right_entry)

            tvWelcomeToStepSync.startAnimation(animationLeftEntry)
            buttonStart.startAnimation(animationLeftEntry)
            artbuttonStart.startAnimation(animationLeftEntry)

            tvWelcomeText.startAnimation(animationRightEntry)

            buttonStart.setOnClickListener{
                findNavController().navigate(R.id.action_welcomeFragment_to_userNameFragment)
            }
        }
    }

}
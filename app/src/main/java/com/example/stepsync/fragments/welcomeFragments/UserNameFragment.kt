package com.example.stepsync.fragments.welcomeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stepsync.R
import com.example.stepsync.databinding.FragmentUserNameBinding
import com.example.stepsync.sharedpreferanceUtils.UserData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserNameFragment: Fragment() {
    private lateinit var binding: FragmentUserNameBinding

    @Inject
    lateinit var userData: UserData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserNameBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun validateUserName(): Boolean {
        if (binding.edUserName.text.isNullOrBlank()){
            binding.edUserName.error = "Name should not be empty"
            return false
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNext.setOnClickListener {
            if (validateUserName()){
                val userName = binding.edUserName.text.toString()
                userData.setUserName(userName)

                // navigation to next fragment
                findNavController().navigate(R.id.action_userNameFragment_to_userPictureFragment)
            }
        }
    }
}
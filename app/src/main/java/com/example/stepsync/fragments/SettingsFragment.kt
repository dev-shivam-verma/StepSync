package com.example.stepsync.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.stepsync.databinding.FragmentHomeBinding
import com.example.stepsync.databinding.FragmentSettingsBinding
import com.example.stepsync.fragments.welcomeFragments.IMAGE_REQUEST_CODE
import com.example.stepsync.fragments.welcomeFragments.PERMISSION_REQUEST_CODE
import com.example.stepsync.sharedpreferanceUtils.UserData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var userData: UserData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val userName = userData.getUserName()

            setUserImage()

            tvUserNameSettings.text = userName


            ivProfilePicSettings.setOnClickListener {
                val permission = Manifest.permission.READ_MEDIA_IMAGES
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requireActivity().requestPermissions(
                        arrayOf(permission),
                        PERMISSION_REQUEST_CODE
                    )
                } else {
                    // Permission already granted, proceed to pick image
                    pickImage()
                }
            }
        }
    }

    private fun setUserImage() {
        val userBitmap = userData.getUserPic()
        binding.ivProfilePicSettings.setImageBitmap(userBitmap)
        binding.ivProfilePicSettings.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private fun pickImage() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
            startActivityForResult(it, IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uriString: Uri = data.data!!

            userData.setUserPicUri(uriString.toString())
            userData.setUserOld()

            setUserImage()
        }
    }

}
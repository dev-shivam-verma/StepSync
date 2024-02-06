package com.example.stepsync.fragments.welcomeFragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.drm.DrmStore.Action
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.stepsync.R
import com.example.stepsync.activities.MainActivity
import com.example.stepsync.databinding.FragmentUserPictureBinding
import com.example.stepsync.sharedpreferanceUtils.UserData
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val IMAGE_REQUEST_CODE = 1
const val PERMISSION_REQUEST_CODE = 1

@AndroidEntryPoint
class UserPictureFragment : Fragment() {
    private lateinit var binding: FragmentUserPictureBinding

    @Inject
    lateinit var userData: UserData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialUi()

        binding.buttonUploadPic.setOnClickListener {
            val permission = Manifest.permission.READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requireActivity().requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
            } else {
                // Permission already granted, proceed to pick image
                pickImage()
            }
        }


    }

    private fun setupInitialUi() {
        binding.ivUserImage.visibility = View.GONE
        binding.buttonNext.setOnClickListener {
            Snackbar.make(
                requireContext(),
                binding.root,
                "Upload Picture first",
                Snackbar.LENGTH_SHORT
            ).show()
        }
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

            changeUI()
        }
    }

    private fun changeUI() {
        binding.apply {
            ivUserImage.visibility = View.VISIBLE
            userPictureLottieAnimation.visibility = View.INVISIBLE

            val userBitmap = userData.getUserPic()

            ivUserImage.setImageBitmap(userBitmap)
            ivUserImage.scaleType = ImageView.ScaleType.CENTER_CROP

            buttonNext.setOnClickListener {
                Intent(requireContext(), MainActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }
}
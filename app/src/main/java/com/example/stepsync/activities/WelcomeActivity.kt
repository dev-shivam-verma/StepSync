package com.example.stepsync.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.stepsync.databinding.ActivityWelcomeBinding
import com.example.stepsync.sharedpreferanceUtils.UserData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity: AppCompatActivity() {
    private val binding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var userData : UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        if (!userData.isUserNew()){
            Intent(this,MainActivity::class.java).also {
                startActivity(it)
            }
        }


    }
}
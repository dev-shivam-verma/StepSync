package com.example.stepsync.activities

import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.stepsync.R
import com.example.stepsync.databinding.ActivityMainBinding
import com.example.stepsync.fragments.NewProjectFragment
import com.example.stepsync.viewModel.ProjectsViewModel
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var viewModel: ProjectsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSystemUi()
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.FragmentHostViewMain)
        binding.bottomNavigation.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab){
                when(newIndex){
                    0 -> navController.navigate(R.id.homeFragment)
                    1 -> navController.navigate(R.id.settingsFragment)
                }
            }
        })

        navController.addOnDestinationChangedListener(listener = NavController.OnDestinationChangedListener(){ NavController, NavDestination, Bundle ->
            when(NavDestination.id){
                R.id.homeFragment -> {
                    binding.bottomNavigation.selectTabAt(0)
                }
                R.id.settingsFragment -> {
                    binding.bottomNavigation.selectTabAt(1)
                }
            }
        })


    }

    private fun setupSystemUi() {
        enableEdgeToEdge()
        window.navigationBarColor = getColor(R.color.black)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}
package com.app.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.app.demo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        val bundle: Bundle? = intent.extras

        if (bundle != null) {
            val profileId = intent.getStringExtra("publisher")
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId)
                .apply()
        }
    }
}
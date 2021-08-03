package com.app.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.demo.databinding.ActivityMainBinding
import com.app.demo.fragments.HomeFragment
import com.app.demo.fragments.NotificationFragment
import com.app.demo.fragments.ProfileFragment
import com.app.demo.fragments.SearchFragment
import com.app.demo.utils.openActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectorFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->

            selectorFragment = when (menuItem.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_search -> SearchFragment()
                R.id.nav_add -> {
                    openActivity(PostActivity::class.java)
                    null
                }
                R.id.nav_heart -> NotificationFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> null
            }
            if (selectorFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    selectorFragment!!).addToBackStack(null).commit()
            }
            true
        }

        val bundle: Bundle? = intent.extras

        if (bundle != null) {
            val profileId = intent.getStringExtra("publisher")

            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId)
                .apply()

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                ProfileFragment()).commit()
            if (profileId != null) {
                binding.bottomNavigation.selectedItemId = R.id.nav_profile
            } else {
                binding.bottomNavigation.selectedItemId = R.id.nav_home
            }

        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HomeFragment()).commit()
        }
    }
}
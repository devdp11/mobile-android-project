package com.example.android_studio_project.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.android_studio_project.R
import com.example.android_studio_project.databinding.ActivityMainBinding
import com.example.android_studio_project.fragment.ot.display_home
import com.example.android_studio_project.fragment.ot.display_search
import com.example.android_studio_project.fragment.profile.display.display_profile

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(display_home())
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.home -> replaceFragment(display_home())
                R.id.search -> replaceFragment(display_search())
                R.id.profile -> replaceFragment(display_profile())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
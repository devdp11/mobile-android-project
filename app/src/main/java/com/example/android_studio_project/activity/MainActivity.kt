package com.example.android_studio_project.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.AuthService
import com.example.android_studio_project.databinding.ActivityMainBinding
import com.example.android_studio_project.fragment.ot.display_home
import com.example.android_studio_project.fragment.ot.display_search
import com.example.android_studio_project.fragment.profile.display.display_profile

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = AuthService(this)

        val userEmail = authService.getUserEmail()
        if (userEmail != null) {
            replaceFragment(display_home(userEmail))
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val userEmail = authService.getUserEmail()
                    if (userEmail != null) {
                        replaceFragment(display_home(userEmail))
                    } else {
                        redirectToLogin()
                    }
                }
                R.id.search -> {
                    val userEmail = authService.getUserEmail()
                    if (userEmail != null) {
                        replaceFragment(display_search(userEmail))
                    } else {
                        redirectToLogin()
                    }
                }
                R.id.profile -> {
                    val userEmail = authService.getUserEmail()
                    if (userEmail != null) {
                        replaceFragment(display_profile(userEmail))
                    } else {
                        redirectToLogin()
                    }
                }
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

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

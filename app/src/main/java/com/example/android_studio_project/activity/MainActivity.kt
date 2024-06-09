package com.example.android_studio_project.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "Exception in thread ${thread.name}", throwable)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = AuthService(this)

        val userEmail = authService.getUserEmail()
        val userUUID = authService.getUserUUID()

        if (userEmail != null && userUUID != null) {
            replaceFragment(display_home(userEmail, userUUID))
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    if (userEmail != null && userUUID != null) {
                        replaceFragment(display_home(userEmail, userUUID))
                    } else {
                        redirectToLogin()
                    }
                }
                R.id.search -> {
                    if (userEmail != null) {
                        userUUID?.let { display_search(userEmail, it) }?.let { replaceFragment(it) }
                    } else {
                        redirectToLogin()
                    }
                }
                R.id.profile -> {
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

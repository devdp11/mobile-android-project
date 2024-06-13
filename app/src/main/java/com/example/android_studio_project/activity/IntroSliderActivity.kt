package com.example.android_studio_project.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android_studio_project.R
import com.example.android_studio_project.utils.LocaleHelper

class IntroSliderActivity : AppCompatActivity() {
    private lateinit var mainActivity: MainActivity
    private lateinit var navController: NavController
    private val handler = android.os.Handler()
    private val timing = 10000
    private val fragments = listOf(R.id.one, R.id.two, R.id.three)
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)

        if (isLoggedIn()) {
            navigateToDashboard()
            return
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        val btnSkip: Button = findViewById(R.id.btn_skip)
        btnSkip.setOnClickListener {
            navigateToLogin()
        }

        LocaleHelper.loadLocale(this)

        val btnBack: Button = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            navigateToPrevPage()
        }

        val btnNext: Button = findViewById(R.id.btn_next)
        btnNext.setOnClickListener {
            navigateToNextPage()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startAutoNavigation()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startAutoNavigation() {
        handler.postDelayed({
            navigateToNextPage()
        }, timing.toLong())
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToNextPage() {
        currentIndex = (currentIndex + 1) % fragments.size
        navController.navigate(fragments[currentIndex])

        handler.removeCallbacksAndMessages(null)
        startAutoNavigation()
    }

    private fun navigateToPrevPage() {
        currentIndex = if (currentIndex > 0) currentIndex - 1 else fragments.size - 1
        navController.navigate(fragments[currentIndex])

        handler.removeCallbacksAndMessages(null)
        startAutoNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

package com.example.android_studio_project.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.AuthService
import com.example.android_studio_project.databinding.ActivityMainBinding
import com.example.android_studio_project.fragment.no_wifi
import com.example.android_studio_project.fragment.ot.display_home
import com.example.android_studio_project.fragment.ot.display_search
import com.example.android_studio_project.fragment.profile.display.display_profile
import com.example.android_studio_project.utils.LocaleHelper
import com.example.android_studio_project.utils.NetworkUtils
import android.content.BroadcastReceiver

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authService: AuthService

    private var currentFragmentTag: String? = null

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                updateConnectionState()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "Exception in thread ${thread.name}", throwable)
        }

        LocaleHelper.loadLocale(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = AuthService(this)

        val userEmail = authService.getUserEmail()
        val userUUID = authService.getUserUUID()

        if (userEmail != null && userUUID != null) {
            replaceFragment(display_home(userEmail, userUUID), "home")
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val isConnected = NetworkUtils.isNetworkAvailable(this)
            when (menuItem.itemId) {
                R.id.home -> {
                    if (isConnected) {
                        if (userEmail != null && userUUID != null) {
                            replaceFragment(display_home(userEmail, userUUID), "home")
                        } else {
                            redirectToLogin()
                        }
                    } else {
                        replaceFragment(no_wifi(), "no_wifi")
                    }
                }
                R.id.search -> {
                    if (isConnected) {
                        if (userEmail != null) {
                            userUUID?.let { display_search(userEmail, it) }?.let { replaceFragment(it, "search") }
                        } else {
                            redirectToLogin()
                        }
                    } else {
                        replaceFragment(no_wifi(), "no_wifi")
                    }
                }
                R.id.profile -> {
                    if (userEmail != null) {
                        replaceFragment(display_profile(userEmail), "profile")
                    } else {
                        redirectToLogin()
                    }
                }
            }
            true
        }

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)

        updateConnectionState()
    }

    override fun onResume() {
        super.onResume()
        updateConnectionState()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        if (currentFragmentTag != tag) {
            currentFragmentTag = tag
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, fragment, tag)
            fragmentTransaction.commit()
        }
    }

    private fun updateConnectionState() {
        val isConnected = NetworkUtils.isNetworkAvailable(this)
        if (isConnected) {
            val userEmail = authService.getUserEmail()
            val userUUID = authService.getUserUUID()
            if (userEmail != null && userUUID != null) {
                when (currentFragmentTag) {
                    "home" -> replaceFragment(display_home(userEmail, userUUID), "home")
                    "search" -> replaceFragment(display_search(userEmail, userUUID), "search")
                    "profile" -> replaceFragment(display_profile(userEmail), "profile")
                    else -> replaceFragment(display_home(userEmail, userUUID), "home")
                }
            }
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
            if (currentFragment !is display_profile) {
                replaceFragment(no_wifi(), "no_wifi")
            }
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

package com.example.android_studio_project.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.services.AuthService
import com.example.android_studio_project.data.retrofit.services.UserService
import com.example.android_studio_project.data.room.ent.User
import com.example.android_studio_project.data.room.vm.UserViewModel
import com.example.android_studio_project.utils.LocaleHelper
import com.example.android_studio_project.utils.NetworkUtils
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private lateinit var authService: AuthService
    private lateinit var userService: UserService
    private lateinit var userViewModel: UserViewModel

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    }

    private lateinit var passwordField: EditText
    private lateinit var imageViewNoInternet: ImageView
    private var isPasswordVisible: Boolean = false
    private var wasNetworkUnavailable = false

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        monitorNetworkStatus()

        if (isLoggedIn()) {
            checkAndApplyNightMode()
            navigateToDashboard()

            return
        }

        checkAndApplyNightMode()


        LocaleHelper.loadLocale(this)

        authService = AuthService(this)
        userService = UserService(this)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userViewModel.deleteAllUsers()

        passwordField = findViewById(R.id.editTextPassword)
        imageViewNoInternet = findViewById(R.id.imageViewNoInternet)

        passwordField.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (passwordField.right - passwordField.compoundDrawables[drawableEnd].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        val email: EditText = findViewById(R.id.editTextEmail)
        val password = passwordField
        val btnLogin: Button = findViewById(R.id.buttonLogin)
        val linkRegister: TextView = findViewById(R.id.link_register)
        val checkBoxToken: CheckBox = findViewById(R.id.check_box_token)

        val savedEmail = sharedPreferences.getString("user_email", null)
        email.setText(savedEmail)

        linkRegister.setOnClickListener {
            openRegister()
        }

        btnLogin.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val rememberMe = checkBoxToken.isChecked

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                authService.verifyUser(emailText, passwordText, onResponse = { success ->
                    if (success) {
                        getUserDetails(emailText, onResponse = { user ->
                            if (user != null) {
                                val userEntity = user.uuid?.let { it1 ->
                                    User(
                                        uuid = it1,
                                        firstName = user.firstName,
                                        lastName = user.lastName,
                                        username = user.username,
                                        avatar = user.avatar,
                                        email = user.email,
                                        password = passwordText
                                    )
                                }
                                if (userEntity != null) {
                                    userViewModel.addUser(userEntity)
                                }
                                runOnUiThread {
                                    if (rememberMe) {
                                        saveLoginState(true)
                                    }
                                    Toast.makeText(this, getString(R.string.login_succe), Toast.LENGTH_LONG).show()
                                    checkAndApplyNightMode()
                                    navigateToDashboard()
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show()
                                }
                            }
                        }, onFailure = {
                            runOnUiThread {
                                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show()
                        }
                    }
                }, onFailure = {
                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                Toast.makeText(this, getString(R.string.fill_fields), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            isPasswordVisible = false
        } else {
            passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            isPasswordVisible = true
        }
        passwordField.setSelection(passwordField.text.length)
    }

    private fun openRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun checkAndApplyNightMode() {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("NightMode", false)

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }



    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun getUserDetails(email: String, onResponse: (UserModel?) -> Unit, onFailure: (Throwable) -> Unit) {
        userService.getUserDetails(email, onResponse, onFailure)
    }

    private fun monitorNetworkStatus() {
        coroutineScope.launch {
            while (true) {
                val isNetworkAvailable = NetworkUtils.isNetworkAvailable(this@LoginActivity)
                if (isNetworkAvailable) {
                    if (wasNetworkUnavailable) {
                        imageViewNoInternet.setImageResource(R.drawable.yes_wifi)
                        imageViewNoInternet.visibility = android.view.View.VISIBLE
                        delay(5000)
                        imageViewNoInternet.visibility = android.view.View.GONE
                    }
                    wasNetworkUnavailable = false
                } else {
                    imageViewNoInternet.setImageResource(R.drawable.no_wifi)
                    imageViewNoInternet.visibility = android.view.View.VISIBLE
                    wasNetworkUnavailable = true
                }
                delay(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}

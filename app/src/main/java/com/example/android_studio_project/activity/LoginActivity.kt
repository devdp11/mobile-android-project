package com.example.android_studio_project.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.services.AuthService
import com.example.android_studio_project.data.retrofit.services.UserService
import com.example.android_studio_project.data.room.ent.User
import com.example.android_studio_project.data.room.vm.UserViewModel
import com.example.android_studio_project.utils.LocaleHelper
import com.example.android_studio_project.utils.NetworkUtils

class LoginActivity : AppCompatActivity() {
    private lateinit var authService: AuthService
    private lateinit var userService: UserService
    private lateinit var userViewModel: UserViewModel

    private lateinit var passwordField: EditText
    private var isPasswordVisible: Boolean = false

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var connectivityReceiver: ConnectivityReceiver

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (!NetworkUtils.isNetworkAvailable(this)) {
            setContentView(R.layout.no_internet)
            return
        } else {
            setContentView(R.layout.activity_login)
        }

        LocaleHelper.loadLocale(this)
        authService = AuthService(this)
        userService = UserService(this)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        passwordField = findViewById(R.id.editTextPassword)
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
            if (!NetworkUtils.isNetworkAvailable(this)) {
                setContentView(R.layout.no_internet)
                return@setOnClickListener
            }

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

    override fun onResume() {
        super.onResume()
        connectivityReceiver = ConnectivityReceiver()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityReceiver)
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

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun getUserDetails(email: String, onResponse: (UserModel?) -> Unit, onFailure: (Throwable) -> Unit) {
        userService.getUserDetails(email, onResponse, onFailure)
    }

    inner class ConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!context?.let { NetworkUtils.isNetworkAvailable(it) }!!) {
                setContentView(R.layout.no_internet)
            } else {
                setContentView(R.layout.activity_login)
            }
        }
    }
}

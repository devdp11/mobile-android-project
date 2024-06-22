package com.example.android_studio_project.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.services.AuthService
import com.example.android_studio_project.data.room.ent.User
import com.example.android_studio_project.data.room.vm.UserViewModel
import com.example.android_studio_project.utils.LocaleHelper
import com.example.android_studio_project.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.android_studio_project.fragment.ot.password_details

class RegisterActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)

    private lateinit var authService: AuthService
    private lateinit var userViewModel: UserViewModel

    private lateinit var passwordField: EditText
    private var isPasswordVisible: Boolean = false
    private lateinit var imageViewNoInternet: ImageView
    private var wasNetworkUnavailable = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var btnRegister : Button

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        monitorNetworkStatus()

        if (isLoggedIn()) {
            navigateToDashboard()
            return
        }

        LocaleHelper.loadLocale(this)
        authService = AuthService(this)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        btnRegister = findViewById(R.id.buttonRegister)

        val linkLogin: TextView = findViewById(R.id.link_login)
        imageViewNoInternet = findViewById(R.id.imageViewNoInternet)

        val checkBoxToken: CheckBox = findViewById(R.id.check_box_token)

        linkLogin.setOnClickListener {
            openLogin()
        }

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

        val passwordDetailsIcon: ImageView = findViewById(R.id.password_details)
        passwordDetailsIcon.setOnClickListener {
            showPasswordRequirementsDialog(passwordField.text.toString())
        }

        btnRegister.setOnClickListener {
            val firstNameText = findViewById<EditText>(R.id.editTextFirstName).text.toString()
            val lastNameText = findViewById<EditText>(R.id.editTextLastName).text.toString()
            val usernameText = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val emailText = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val passwordText = passwordField.text.toString()
            val rememberMe = checkBoxToken.isChecked

            val passwordPattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!_])(?=\\S+$).{6,}$")
            val isNewPasswordValid = passwordPattern.matches(passwordText)

            if (emailText.isNotEmpty() && passwordText.isNotEmpty() && firstNameText.isNotEmpty() && lastNameText.isNotEmpty() && usernameText.isNotEmpty()) {
                if (isNewPasswordValid) {
                    val newUser = UserModel(
                        uuid = UUID.randomUUID(),
                        firstName = firstNameText,
                        lastName = lastNameText,
                        avatar = null,
                        username = usernameText,
                        email = emailText,
                        password = passwordText
                    )
                    authService.createUser(newUser, onResponse = { responseMessage ->
                        runOnUiThread {
                            when (responseMessage) {
                                "success" -> {
                                    val userEntity = newUser.uuid?.let { it1 ->
                                        User(
                                            uuid = it1,
                                            firstName = newUser.firstName,
                                            lastName = newUser.lastName,
                                            username = newUser.username,
                                            avatar = newUser.avatar,
                                            email = newUser.email
                                        )
                                    }
                                    if (userEntity != null) {
                                        userViewModel.addUser(userEntity)
                                    }

                                    Toast.makeText(this, getString(R.string.register_succe), Toast.LENGTH_LONG).show()
                                    if (rememberMe) {
                                        saveLoginState(true)
                                    }
                                    navigateToDashboard()
                                }
                                "exists" -> {
                                    Toast.makeText(this, getString(R.string.register_error_username_email), Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    Toast.makeText(this, getString(R.string.register_error), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }, onFailure = {
                        runOnUiThread {
                            Toast.makeText(this, getString(R.string.register_error), Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Toast.makeText(this, getString(R.string.password_invalid), Toast.LENGTH_LONG).show()
                }

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

    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
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

    private fun showPasswordRequirementsDialog(password: String) {
        val dialog = password_details.newInstance(password)
        dialog.show(supportFragmentManager, "PasswordDetailsDialog")
    }

    private fun monitorNetworkStatus() {
        coroutineScope.launch {
            while (true) {
                val isNetworkAvailable = NetworkUtils.isNetworkAvailable(this@RegisterActivity)
                if (isNetworkAvailable) {
                    if (wasNetworkUnavailable) {
                        imageViewNoInternet.setImageResource(R.drawable.yes_wifi)
                        imageViewNoInternet.visibility = View.VISIBLE
                        delay(5000)
                        imageViewNoInternet.visibility = View.GONE
                    }
                    btnRegister.isEnabled = true
                    wasNetworkUnavailable = false
                } else {
                    imageViewNoInternet.setImageResource(R.drawable.no_wifi)
                    imageViewNoInternet.visibility = View.VISIBLE
                    btnRegister.isEnabled = false
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

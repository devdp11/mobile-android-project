package com.example.android_studio_project.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent

import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi

import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.services.AuthService

class RegisterActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)

    private lateinit var authService: AuthService
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private var isPasswordVisible: Boolean = false

    @SuppressLint("ClickableViewAccessibility", "CutPasteId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authService = AuthService(this)

        val btnRegister: Button = findViewById(R.id.buttonRegister)

        val linkLogin: TextView = findViewById(R.id.link_login)

        linkLogin.setOnClickListener {
            openLogin()
        }

        passwordField = findViewById(R.id.editTextPassword)

        confirmPasswordField = findViewById(R.id.editTextConfirmPassword)
        confirmPasswordField.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (confirmPasswordField.right - confirmPasswordField.compoundDrawables[drawableEnd].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        btnRegister.setOnClickListener {
            val firstNameText = findViewById<EditText>(R.id.editTextFirstName).text.toString()
            val lastNameText = findViewById<EditText>(R.id.editTextLastName).text.toString()
            val usernameText = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val emailText = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val passwordText = findViewById<EditText>(R.id.editTextPassword).text.toString()

            val newUser = UserModel(
                firstName = firstNameText,
                lastName = lastNameText,
                avatar = null,
                username = usernameText,
                email = emailText,
                password = passwordText
            )
            if (emailText.isNotEmpty() && passwordText.isNotEmpty() && firstNameText.isNotEmpty() && lastNameText.isNotEmpty() && usernameText.isNotEmpty()) {

                authService.createUser(newUser, onResponse = { responseMessage ->
                    runOnUiThread {
                        if (responseMessage == "success") {
                            Toast.makeText(this, getString(R.string.register_succe), Toast.LENGTH_LONG).show()
                            navigateToDashboard()
                        } else {
                            Toast.makeText(this, getString(R.string.register_error), Toast.LENGTH_LONG).show()
                        }
                    }
                }, onFailure = {
                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.register_error), Toast.LENGTH_LONG).show()
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
            confirmPasswordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            isPasswordVisible = false
        } else {
            passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            confirmPasswordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            isPasswordVisible = true
        }
        passwordField.setSelection(passwordField.text.length)
        confirmPasswordField.setSelection(confirmPasswordField.text.length)
    }

    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}



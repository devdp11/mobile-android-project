package com.example.android_studio_project.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.AuthService

class LoginActivity : AppCompatActivity() {
    private lateinit var authService: AuthService

    private lateinit var passwordField: EditText
    private var isPasswordVisible: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authService = AuthService(this)

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

        linkRegister.setOnClickListener {
            openRegister()
        }

        btnLogin.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                authService.verifyUser(emailText, passwordText, onResponse = { success ->
                    if (success) {
                        runOnUiThread {
                            Toast.makeText(this, getString(R.string.login_succe), Toast.LENGTH_LONG).show()
                        }
                        navigateToDashboard()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show()
                        }
                    }
                }, onFailure = { error ->
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
}

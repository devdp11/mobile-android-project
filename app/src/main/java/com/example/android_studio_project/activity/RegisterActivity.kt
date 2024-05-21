package com.example.android_studio_project.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.InputType

import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi

import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.services.AuthService

class RegisterActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)

    private lateinit var authService: AuthService


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authService = AuthService(this)

        val btnVisibility: ImageButton = findViewById(R.id.togglePasswordVisibility)
        val passwordText: EditText = findViewById(R.id.editTextPassword)

        var isPasswordVisible = false

        btnVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                btnVisibility.setImageResource(R.drawable.eye2)
                passwordText.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                btnVisibility.setImageResource(R.drawable.eye1)
                passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            passwordText.setSelection(passwordText.text.length)
        }

        val btnRegister: Button = findViewById(R.id.buttonRegister)

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

    fun openLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)}
}



package com.example.android_studio_project.activity

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_studio_project.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
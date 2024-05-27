package com.example.android_studio_project.fragment.profile.password

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.AuthService
import com.example.android_studio_project.data.retrofit.services.UserService

class edit_password(private val userEmail: String) : Fragment() {
    private lateinit var authService: AuthService
    private lateinit var userService: UserService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authService = AuthService(requireContext())
        userService = UserService(requireContext())

        val view = inflater.inflate(R.layout.fragment_edit_password, container, false)

        val oldPasswordEditText = view.findViewById<EditText>(R.id.old_password)
        val newPasswordEditText = view.findViewById<EditText>(R.id.new_password)
        val confirmNewPasswordEditText = view.findViewById<EditText>(R.id.confirm_password)

        val updateButton = view.findViewById<Button>(R.id.save_btn)
        updateButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmNewPassword = confirmNewPasswordEditText.text.toString()

            if (newPassword == confirmNewPassword) {
                authService.verifyUser(userEmail, oldPassword, { isOldPasswordCorrect ->
                    if (isOldPasswordCorrect) {
                        userService.updateUserPassword(newPassword, {
                            Toast.makeText(
                                requireContext(),
                                "Password updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }, { error ->
                            Log.e("EditPasswordFragment", "Error updating password", error)
                            Toast.makeText(requireContext(), "Error updating password", Toast.LENGTH_SHORT).show()
                        })
                    } else {
                        Toast.makeText(requireContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("EditPasswordFragment", "Error verifying old password", error)
                    Toast.makeText(requireContext(), "Error verifying old password", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }

    companion object {
        fun newInstance(userEmail: String): edit_password {
            return edit_password(userEmail)
        }
    }
}

package com.example.android_studio_project.fragment.profile.password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.AuthService
import com.example.android_studio_project.data.retrofit.services.UserService
import com.example.android_studio_project.utils.NetworkUtils
import com.example.android_studio_project.fragment.no_wifi
import androidx.fragment.app.FragmentActivity

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

        val passwordRequirementsTextOne = view.findViewById<TextView>(R.id.password_requirements_text_one)
        val passwordRequirementsTextTwo = view.findViewById<TextView>(R.id.password_requirements_text_two)
        val passwordRequirementsTextThree = view.findViewById<TextView>(R.id.password_requirements_text_three)
        val passwordRequirementsTextFour = view.findViewById<TextView>(R.id.password_requirements_text_four)

        val backButton: ImageView = view.findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        newPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()

                if (password.any { it.isUpperCase() }) {
                    passwordRequirementsTextOne.setTextColor(resources.getColor(R.color.green))
                } else {
                    passwordRequirementsTextOne.setTextColor(resources.getColor(R.color.red))
                }

                if (password.any { it.isLowerCase() }) {
                    passwordRequirementsTextTwo.setTextColor(resources.getColor(R.color.green))
                } else {
                    passwordRequirementsTextTwo.setTextColor(resources.getColor(R.color.red))
                }

                if (password.any { it.isDigit() }) {
                    passwordRequirementsTextThree.setTextColor(resources.getColor(R.color.green))
                } else {
                    passwordRequirementsTextThree.setTextColor(resources.getColor(R.color.red))
                }

                if (password.any { "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~".contains(it) }) {
                    passwordRequirementsTextFour.setTextColor(resources.getColor(R.color.green))
                } else {
                    passwordRequirementsTextFour.setTextColor(resources.getColor(R.color.red))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val updateButton = view.findViewById<Button>(R.id.save_btn)
        updateButton.setOnClickListener {
            val isConnected = NetworkUtils.isNetworkAvailable(requireContext())
            if (!isConnected) {
                switchToNoWifiFragment()
                return@setOnClickListener
            }

            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmNewPassword = confirmNewPasswordEditText.text.toString()

            val passwordPattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!_])(?=\\S+$).{4,}$")
            val isNewPasswordValid = passwordPattern.matches(newPassword)

            if (oldPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty()) {
                if (newPassword == confirmNewPassword) {
                    if (newPassword != oldPassword) {
                        if (isNewPasswordValid) {
                            authService.verifyUser(userEmail, oldPassword, { isOldPasswordCorrect ->
                                if (isOldPasswordCorrect) {
                                    userService.updateUserPassword(newPassword, {
                                        Toast.makeText(requireContext(), getString(R.string.update_password_succe), Toast.LENGTH_SHORT).show()
                                        requireActivity().onBackPressed()
                                    }, {
                                        Toast.makeText(requireContext(), getString(R.string.update_password_error), Toast.LENGTH_SHORT).show()
                                    })
                                } else {
                                    Toast.makeText(requireContext(), getString(R.string.old_password_incorr), Toast.LENGTH_SHORT).show()
                                }
                            }, {
                                Toast.makeText(requireContext(), getString(R.string.old_password_verify_error), Toast.LENGTH_SHORT).show()
                            })
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.password_invalid), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.new_password_same_as_old_error), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.new_password_error), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun switchToNoWifiFragment() {
        val fragmentActivity: FragmentActivity? = activity
        if (fragmentActivity != null && isAdded) {
            fragmentActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, no_wifi())
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        fun newInstance(userEmail: String): edit_password {
            return edit_password(userEmail)
        }
    }
}

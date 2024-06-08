package com.example.android_studio_project.fragment.profile.display

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.util.Base64
import android.widget.Toast
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.UserService
import com.example.android_studio_project.fragment.profile.edit.edit_profile
import com.bumptech.glide.Glide
import com.example.android_studio_project.activity.LoginActivity
import com.example.android_studio_project.fragment.profile.password.edit_password

class display_profile(private val userEmail: String) : Fragment() {
    private lateinit var userService: UserService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_profile, container, false)

        val editProfile: Button = view.findViewById(R.id.btn_edit)
        editProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, edit_profile.newInstance(userEmail))
                .addToBackStack(null)
                .commit()
        }

        val editPassword: ImageView = view.findViewById(R.id.security_btn)
        editPassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, edit_password.newInstance(userEmail))
                .addToBackStack(null)
                .commit()
        }

        userService = UserService(requireContext())

        val textViewName: TextView = view.findViewById(R.id.user_name)
        val textViewEmail: TextView = view.findViewById(R.id.user_mail)
        val imageViewAvatar: ImageView = view.findViewById(R.id.user_avatar)

        userService.getUserDetails(
            onResponse = { userDetails ->
                userDetails?.let {
                    textViewName.text = userDetails.firstName ?: ""
                    textViewEmail.text = userDetails.email ?: ""

                    val userAvatarBase64: String? = userDetails.avatar

                    if (!userAvatarBase64.isNullOrEmpty()) {
                        val userAvatarBytes = Base64.decode(userAvatarBase64, Base64.DEFAULT)
                        Glide.with(this)
                            .load(userAvatarBytes)
                            .into(imageViewAvatar)
                    } else {
                        imageViewAvatar.setImageResource(R.drawable.default_image)
                    }

                }
            },
            onFailure = {
                Toast.makeText(context, getString(R.string.load_error), Toast.LENGTH_SHORT).show()
            }
        )

        val logoutBtn: Button = view.findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            saveLoginState(false)
            navigateToLogin()
        }

        return view
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        fun newInstance(userEmail: String): display_profile {
            return display_profile(userEmail)
        }
    }
}

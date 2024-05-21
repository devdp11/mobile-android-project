package com.example.android_studio_project.fragment.profile.edit

import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.UserService

class edit_profile(private val userEmail: String) : Fragment() {
    private lateinit var userService: UserService
    private lateinit var textViewFirstName: EditText
    private lateinit var textViewLastName: EditText
    private lateinit var textViewEmail: TextView
    private lateinit var textViewUsername: EditText
    private lateinit var imageViewAvatar: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        textViewFirstName = view.findViewById(R.id.first_name)
        textViewLastName = view.findViewById(R.id.last_name)
        textViewEmail = view.findViewById(R.id.user_email)
        textViewUsername = view.findViewById(R.id.username)
        imageViewAvatar = view.findViewById(R.id.user_avatar)

        userService = UserService(requireContext())

        imageViewAvatar = view.findViewById(R.id.user_avatar)

        userService.getUserDetails(
            onResponse = { userDetails ->
                userDetails?.let {
                    textViewFirstName.text = userDetails.firstName?.toEditable()
                    textViewLastName.text = userDetails.lastName?.toEditable()
                    textViewUsername.text = userDetails.username?.toEditable()
                    textViewEmail.text = userDetails.email ?: ""

                    val userAvatarUrl: String? = userDetails.avatar

                    if (!userAvatarUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(userAvatarUrl)
                            .into(imageViewAvatar)
                    } else {
                        imageViewAvatar.setImageResource(R.drawable.profile)
                    }
                }
            },
            onFailure = { error ->
            }
        )

        return view
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    companion object {
        fun newInstance(userEmail: String): edit_profile {
            return edit_profile(userEmail)
        }
    }
}

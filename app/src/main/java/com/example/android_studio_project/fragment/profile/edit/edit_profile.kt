package com.example.android_studio_project.fragment.profile.edit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.services.UserService
import java.io.ByteArrayOutputStream

class edit_profile(private val userEmail: String) : Fragment() {
    private lateinit var userService: UserService
    private lateinit var ViewFirstName: EditText
    private lateinit var ViewLastName: EditText
    private lateinit var textViewEmail: TextView
    private lateinit var ViewUsername: EditText
    private lateinit var imageViewAvatar: ImageView
    private lateinit var btnUpdateProfile: Button

    private var selectedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        ViewFirstName = view.findViewById(R.id.first_name)
        ViewLastName = view.findViewById(R.id.last_name)
        textViewEmail = view.findViewById(R.id.user_email)
        ViewUsername = view.findViewById(R.id.username)
        imageViewAvatar = view.findViewById(R.id.user_avatar)
        btnUpdateProfile = view.findViewById(R.id.save_btn)

        userService = UserService(requireContext())

        val backButton: ImageView = view.findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        userService.getUserDetails(
            onResponse = { userDetails ->
                userDetails?.let {
                    ViewFirstName.text = userDetails.firstName?.toEditable()
                    ViewLastName.text = userDetails.lastName?.toEditable()
                    ViewUsername.text = userDetails.username?.toEditable()
                    textViewEmail.text = userDetails.email ?: ""

                    val userAvatarBase64: String? = userDetails.avatar

                    if (!userAvatarBase64.isNullOrEmpty()) {
                        val userAvatarUrl = Base64.decode(userAvatarBase64, Base64.DEFAULT)
                        Glide.with(this)
                            .asBitmap()
                            .load(userAvatarUrl)
                            .into(imageViewAvatar)
                    } else {
                        imageViewAvatar.setImageResource(R.drawable.default_image)
                    }

                }
            },
            onFailure = { error ->
                Toast.makeText(context, getString(R.string.load_error), Toast.LENGTH_SHORT).show()
            }
        )

        imageViewAvatar.setOnClickListener {
            openGalleryForImage()
        }

        btnUpdateProfile.setOnClickListener {
            val firstName = ViewFirstName.text.toString()
            val lastName = ViewLastName.text.toString()
            val username = ViewUsername.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && username.isNotEmpty()) {
                val avatarBase64 = convertBitmapToBase64(selectedImageBitmap)
                val updatedUser = UserModel(null, firstName, lastName, avatarBase64, username, null, userEmail, false)

                userService.updateUser(updatedUser,
                    onResponse = { updatedUser ->
                        if (updatedUser != null) {
                            Toast.makeText(context, getString(R.string.update_succe), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, getString(R.string.update_error), Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = { error ->
                        Toast.makeText(context, getString(R.string.update_error), Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImage)
            imageViewAvatar.setImageBitmap(selectedImageBitmap)
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun convertBitmapToBase64(bitmap: Bitmap?): String? {
        bitmap?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val byteArray = outputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        return null
    }

    companion object {
        fun newInstance(userEmail: String): edit_profile {
            return edit_profile(userEmail)
        }
    }
}

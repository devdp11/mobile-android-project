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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.services.UserService
import com.example.android_studio_project.data.room.vm.UserViewModel
import com.example.android_studio_project.fragment.profile.display.display_profile
import com.example.android_studio_project.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class edit_profile(private val userEmail: String) : Fragment() {
    private lateinit var userService: UserService
    private lateinit var userViewModel: UserViewModel
    private lateinit var ViewFirstName: EditText
    private lateinit var ViewLastName: EditText
    private lateinit var textViewEmail: TextView
    private lateinit var ViewUsername: EditText
    private lateinit var imageViewAvatar: ImageView
    private lateinit var btnUpdateProfile: Button

    private var selectedImageBitmap: Bitmap? = null

    private var userDetailsLoaded = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

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

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userService = UserService(requireContext())

        monitorNetworkStatus()

        val backButton: ImageView = view.findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, display_profile.newInstance(userEmail))
                .commit()
        }

        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, display_profile.newInstance(userEmail))
                .commit()
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
                val imageAvatar = captureScreenshot(imageViewAvatar)
                val avatarBase64 = convertBitmapToBase64(imageAvatar)
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

    private fun monitorNetworkStatus() {
        val currentContext = context ?: return
        coroutineScope.launch {
            var previousNetworkStatus = false
            while (true) {
                val isNetworkAvailable = NetworkUtils.isNetworkAvailable(currentContext)
                if (isNetworkAvailable && !previousNetworkStatus) {
                    if (!userDetailsLoaded) {
                        getUserDetails()
                        userDetailsLoaded = true
                    }
                } else if (!isNetworkAvailable) {
                    if (!userDetailsLoaded) {
                        getUserDetails()
                        userDetailsLoaded = true
                    }
                }
                previousNetworkStatus = isNetworkAvailable
                delay(1000)
            }
        }
    }

    private fun getUserDetails() {
        if (userDetailsLoaded) return

        val isNetworkAvailable = NetworkUtils.isNetworkAvailable(requireContext())
        if (isNetworkAvailable) {
            userService.getUserDetails(
                onResponse = { userDetails ->
                    if (isAdded) {
                        userDetails?.let {
                            ViewFirstName.text = userDetails.firstName?.toEditable()
                            ViewLastName.text = userDetails.lastName?.toEditable()
                            ViewUsername.text = userDetails.username?.toEditable()
                            textViewEmail.text = userDetails.email ?: ""

                            val userAvatarBase64: String? = userDetails.avatar

                            if (!userAvatarBase64.isNullOrEmpty()) {
                                val userAvatarBytes = Base64.decode(userAvatarBase64, Base64.DEFAULT)
                                Glide.with(requireContext())
                                    .load(userAvatarBytes)
                                    .into(imageViewAvatar)
                            } else {
                                imageViewAvatar.setImageResource(R.drawable.default_image)
                            }

                            userDetailsLoaded = true
                        }
                    }
                },
                onFailure = {
                    if (isAdded) {
                        Toast.makeText(context, getString(R.string.load_error), Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            userViewModel.getUserByEmail(userEmail).observe(viewLifecycleOwner) { user ->
                user?.let {
                    val userAvatarBase64: String? = user.avatar

                    ViewFirstName.text = user.firstName?.toEditable()
                    ViewLastName.text = user.lastName?.toEditable()
                    ViewUsername.text = user.username?.toEditable()
                    textViewEmail.text = user.email ?: ""

                    if (!userAvatarBase64.isNullOrEmpty()) {
                        val userAvatarBytes = Base64.decode(userAvatarBase64, Base64.DEFAULT)
                        Glide.with(requireContext())
                            .load(userAvatarBytes)
                            .into(imageViewAvatar)
                    } else {
                        imageViewAvatar.setImageResource(R.drawable.default_image)
                    }

                    userDetailsLoaded = true
                }
            }
        }
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

    private fun captureScreenshot(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun convertBitmapToBase64(bitmap: Bitmap?): String? {
        bitmap?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
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

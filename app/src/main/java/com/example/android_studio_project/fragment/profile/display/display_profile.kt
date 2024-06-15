package com.example.android_studio_project.fragment.profile.display

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.util.Base64
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.UserService
import com.example.android_studio_project.fragment.profile.edit.edit_profile
import com.bumptech.glide.Glide
import com.example.android_studio_project.activity.LoginActivity
import com.example.android_studio_project.fragment.profile.password.edit_password
import java.util.Locale

class display_profile : Fragment() {

    private lateinit var userService: UserService
    private lateinit var nightModeSwitch: SwitchCompat

    companion object {
        private const val ARG_USER_EMAIL = "userEmail"

        fun newInstance(userEmail: String): display_profile {
            val fragment = display_profile()
            val args = Bundle()
            args.putString(ARG_USER_EMAIL, userEmail)
            fragment.arguments = args
            return fragment
        }
    }

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString(ARG_USER_EMAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_display_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editProfile: Button = view.findViewById(R.id.btn_edit)
        editProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, edit_profile.newInstance(userEmail ?: ""))
                .addToBackStack(null)
                .commit()
        }

        val editPassword: ImageView = view.findViewById(R.id.security_btn)
        editPassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, edit_password.newInstance(userEmail ?: ""))
                .addToBackStack(null)
                .commit()
        }

        userService = UserService(requireContext())

        val textViewName: TextView = view.findViewById(R.id.user_name)
        val textViewEmail: TextView = view.findViewById(R.id.user_mail)
        val imageViewAvatar: ImageView = view.findViewById(R.id.user_avatar)

        userService.getUserDetails(
            onResponse = { userDetails ->
                if (isAdded) {
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
                }
            },
            onFailure = {
                if (isAdded) {
                    Toast.makeText(context, getString(R.string.load_error), Toast.LENGTH_SHORT).show()
                }
            }
        )

        nightModeSwitch = view.findViewById(R.id.night_mode_switch)

        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("NightMode", false)
        nightModeSwitch.isChecked = isNightMode

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        nightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveNightModeState(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveNightModeState(false)
            }
        }

        val logoutBtn: Button = view.findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            showConfirmationDialog()
        }

        val languageBtn: ImageView = view.findViewById(R.id.language_btn)
        languageBtn.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun saveNightModeState(isNightMode: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("NightMode", isNightMode)
        editor.apply()
    }

    private fun showLanguageDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_language, null)
        builder.setView(dialogView)

        val radioGroupLanguages: RadioGroup = dialogView.findViewById(R.id.radioGroupLanguages)
        val radioEnglish: RadioButton = dialogView.findViewById(R.id.radioEnglish)
        val radioPortuguese: RadioButton = dialogView.findViewById(R.id.radioPortuguese)
        val buttonConfirm: Button = dialogView.findViewById(R.id.buttonConfirm)

        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val currentLanguage = sharedPreferences.getString("Language", "en")

        when (currentLanguage) {
            "en" -> radioEnglish.isChecked = true
            "pt" -> radioPortuguese.isChecked = true
        }

        val dialog = builder.create()

        buttonConfirm.setOnClickListener {
            val selectedLanguage = when (radioGroupLanguages.checkedRadioButtonId) {
                R.id.radioEnglish -> "en"
                R.id.radioPortuguese -> "pt"
                else -> "en"
            }
            setLocale(selectedLanguage)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Language", languageCode)
        editor.apply()

        val refresh = Intent(requireContext(), requireActivity()::class.java)
        startActivity(refresh)
        requireActivity().finish()
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.logout_desc))
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            saveLoginState(false)
            navigateToLogin()
        }
        val dialog = builder.create()
        dialog.show()
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
}

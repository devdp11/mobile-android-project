package com.example.android_studio_project.fragment.ot

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.android_studio_project.R

class password_details : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requirementOne = view.findViewById<TextView>(R.id.password_requirements_text_one)
        val requirementTwo = view.findViewById<TextView>(R.id.password_requirements_text_two)
        val requirementThree = view.findViewById<TextView>(R.id.password_requirements_text_three)
        val requirementFour = view.findViewById<TextView>(R.id.password_requirements_text_four)

        val passwordText = arguments?.getString("password") ?: ""

        val colorValid = ContextCompat.getColor(requireContext(), R.color.green)
        val colorInvalid = ContextCompat.getColor(requireContext(), R.color.security)

        requirementOne.setTextColor(if (passwordText.contains(Regex("(?=.*[A-Z])"))) colorValid else colorInvalid)
        requirementTwo.setTextColor(if (passwordText.contains(Regex("(?=.*[a-z])"))) colorValid else colorInvalid)
        requirementThree.setTextColor(if (passwordText.contains(Regex("(?=.*[0-9])"))) colorValid else colorInvalid)
        requirementFour.setTextColor(if (passwordText.contains(Regex("(?=.*[@#\$%^&+=!_])"))) colorValid else colorInvalid)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.TransparentDialog)
    }

    companion object {
        fun newInstance(password: String): password_details {
            val fragment = password_details()
            val args = Bundle()
            args.putString("password", password)
            fragment.arguments = args
            return fragment
        }
    }
}

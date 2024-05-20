package com.example.android_studio_project.fragment.profile.display

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.android_studio_project.R
import com.example.android_studio_project.fragment.profile.edit.edit_profile

class display_profile : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_profile, container, false)

        val editProfile: Button = view.findViewById(R.id.btn_edit)
        editProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, edit_profile())
                .addToBackStack(null)
                .commit()
        }

        return view    }

}
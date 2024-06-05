package com.example.android_studio_project.fragment.ot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android_studio_project.R

class display_search(private val userEmail: String) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_search, container, false)

        return view
    }
    companion object {
        fun newInstance(userEmail: String): display_search {
            return display_search(userEmail)
        }
    }
}
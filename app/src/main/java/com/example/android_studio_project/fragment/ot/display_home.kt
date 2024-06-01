package com.example.android_studio_project.fragment.ot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.LocationService
import com.example.android_studio_project.data.retrofit.services.TripService
import com.example.android_studio_project.fragment.trip.add_trip.AddTripFragment
import com.example.android_studio_project.fragment.trip.edit_trip.edit_trip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID

class display_home(private val userEmail: String, private val userUUID: String?) : Fragment() {

    private lateinit var tripService: TripService
    private lateinit var displayHomeAdapter: display_home_adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_home, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        displayHomeAdapter = display_home_adapter(emptyList()) { clickedTrip ->
            clickedTrip.uuid?.let { openEditTripFragment(it, userUUID) }
        }
        recyclerView.adapter = displayHomeAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tripService = TripService(requireContext())
        getTrips()

        val addButton: FloatingActionButton = view.findViewById(R.id.btn_add)
        addButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, AddTripFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun openEditTripFragment(tripUuid: UUID, userUUID: String?) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, edit_trip.newInstance(tripUuid, userUUID))
            .addToBackStack(null)
            .commit()
    }

    private fun getTrips() {
        tripService.getUserTrips(userUUID,
            onResponse = { trips ->
                if (trips != null) {
                    displayHomeAdapter.setData(trips)
                } else {
                }
            }
        ) {
        }
    }

    companion object {
        fun newInstance(userEmail: String, userUUID: String): display_home {
            return display_home(userEmail, userUUID)
        }
    }
}
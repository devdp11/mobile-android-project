package com.example.android_studio_project.fragment.ot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.TripModel
import com.example.android_studio_project.data.retrofit.services.TripService
import com.example.android_studio_project.fragment.trip.edit_trip.edit_trip
import java.util.UUID

class display_search(private val userEmail: String, private val userUUID: String) : Fragment() {

    private lateinit var tripService: TripService
    private lateinit var displayHomeAdapter: display_home_adapter
    private var allTrips: List<TripModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_search, container, false)
        val searchBar: EditText = view.findViewById(R.id.search_bar)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        displayHomeAdapter = display_home_adapter(emptyList()) { clickedTrip ->
            clickedTrip.uuid?.let { openEditTripFragment(it, userUUID) }
        }
        recyclerView.adapter = displayHomeAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tripService = TripService(requireContext())
        getTrips()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTrips(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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
                    allTrips = trips
                    displayHomeAdapter.updateData(trips)
                } else {
                }
            }
        ) {
        }
    }

    private fun filterTrips(query: String) {
        val filteredTrips = allTrips.filter { it.name?.contains(query, ignoreCase = true) == true }
        displayHomeAdapter.updateData(filteredTrips)
    }

    companion object {
        fun newInstance(userEmail: String, userUUID: String): display_search {
            return display_search(userEmail, userUUID)
        }
    }
}

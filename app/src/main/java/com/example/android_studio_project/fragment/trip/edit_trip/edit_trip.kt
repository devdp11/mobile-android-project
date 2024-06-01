package com.example.android_studio_project.fragment.trip.edit_trip

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.TripService
import com.example.android_studio_project.fragment.location.add_location.add_location
import com.example.android_studio_project.fragment.location.edit_location.edit_location
import com.example.android_studio_project.fragment.location.list_location.list_location_adapter
import java.util.UUID

class edit_trip(private val tripUuid: UUID, private val userUUID: String?) : Fragment() {
    private lateinit var tripService: TripService
    private lateinit var listLocationAdapter: list_location_adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_trip, container, false)

        val deleteButton: Button = view.findViewById(R.id.delete_btn)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(tripUuid)
        }

        val locationButton: Button = view.findViewById(R.id.add_location_btn)
        locationButton.setOnClickListener {
            openAddLocationFragment(tripUuid)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        listLocationAdapter = list_location_adapter(emptyList()) { clickedLocation ->
            openEditLocationFragment(clickedLocation.uuid, tripUuid)
        }
        recyclerView.adapter = listLocationAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tripService = TripService(requireContext())
        getLocations()

        val textViewName: TextView = view.findViewById(R.id.trip_name)
        val textViewDescription: TextView = view.findViewById(R.id.trip_description)
        val textViewDate: TextView = view.findViewById(R.id.trip_date)
        val rateViewRating: RatingBar = view.findViewById(R.id.trip_rating)

        tripService.getTripById(tripUuid,
            onResponse = { tripDetails ->
                tripDetails?.let {
                    textViewName.text = tripDetails.name ?: ""
                    textViewDescription.text = tripDetails.description ?: ""
                    textViewDate.text = (tripDetails.startDate ?: "").toString()
                    tripDetails.rating?.let { rating ->
                        val roundedRating = rating.toInt()
                        rateViewRating.rating = roundedRating.toFloat()
                    }
                }
            },
            onFailure = {
                Toast.makeText(context, getString(R.string.load_user_error), Toast.LENGTH_SHORT).show()
            }
        )

        return view
    }

    private fun openEditLocationFragment(locationUuid: UUID, tripUuid: UUID) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, edit_location.newInstance(locationUuid, tripUuid))
            .addToBackStack(null)
            .commit()
    }

    private fun getLocations() {
        tripService.getTripLocations(tripUuid,
            onResponse = { locations ->
                if (locations != null) {
                    listLocationAdapter.setData(locations)
                    Toast.makeText(requireContext(), getString(R.string.app_name), Toast.LENGTH_SHORT).show()
                }
            }
        ) {
        }
    }

    private fun showDeleteConfirmationDialog(uuid: UUID) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_title))
        builder.setMessage(getString(R.string.delete_description))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            tripService.deleteTrip(userUUID, uuid,
                onResponse = {
                    Toast.makeText(requireContext(), getString(R.string.trip_delete_succ), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), getString(R.string.trip_delete_error), Toast.LENGTH_SHORT).show()
                    Log.e("DeleteTrip", "Error deleting trip: $error")
                }
            )
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun openAddLocationFragment(tripUuid: UUID) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, add_location.newInstance(tripUuid))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance(tripUuid: UUID, userUUID: String?): edit_trip {
            return edit_trip(tripUuid, userUUID)
        }
    }
}

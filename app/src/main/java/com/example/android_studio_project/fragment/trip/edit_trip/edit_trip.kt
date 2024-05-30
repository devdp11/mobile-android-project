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
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.TripService
import com.example.android_studio_project.fragment.location.add_location.add_location
import java.util.UUID

class edit_trip(private val tripUuid: UUID, private val userUUID: String?) : Fragment() {
    private lateinit var tripService: TripService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_trip, container, false)

        val uuidTextView: TextView = view.findViewById(R.id.uuid_teste)
        val combinedUuids = "Trip UUID: $tripUuid\nUser UUID: $userUUID"
        uuidTextView.text = combinedUuids

        val deleteButton: Button = view.findViewById(R.id.delete_btn)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(tripUuid)
        }

        val locationButton: Button = view.findViewById(R.id.add_location_btn)
        locationButton.setOnClickListener {
            openAddLocationFragment(tripUuid)
        }

        tripService = TripService(requireContext())

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

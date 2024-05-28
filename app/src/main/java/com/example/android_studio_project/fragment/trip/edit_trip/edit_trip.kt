package com.example.android_studio_project.fragment.trip.edit_trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.TripService
import com.google.android.material.textfield.TextInputEditText
import java.util.UUID

class edit_trip(private val tripUuid: UUID) : Fragment() {
    private lateinit var tripService: TripService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_trip, container, false)

        val uuidTextView: TextView = view.findViewById(R.id.uuid_teste)
        uuidTextView.text = tripUuid.toString()

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

    companion object {
        fun newInstance(tripUuid: UUID): edit_trip {
            return edit_trip(tripUuid)
        }
    }
}

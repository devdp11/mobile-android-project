package com.example.android_studio_project.fragment.trip.add_trip

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.TripModel
import com.example.android_studio_project.data.retrofit.services.TripService
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTripFragment : Fragment() {

    private lateinit var tripNameEditText: EditText
    private lateinit var tripDateEditText: EditText
    private lateinit var tripDescriptionEditText: EditText
    private lateinit var tripRatingBar: RatingBar

    private lateinit var saveTripButton: Button
    private lateinit var tripService: TripService

    private lateinit var tripStartDate: Date
    private lateinit var tripEndDate: Date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_trip, container, false)

        tripNameEditText = view.findViewById(R.id.trip_name)
        tripDateEditText = view.findViewById(R.id.trip_date)
        tripDateEditText.setOnClickListener {
            showDatePicker()
        }
        tripDescriptionEditText = view.findViewById(R.id.trip_description)
        tripRatingBar = view.findViewById(R.id.trip_rating)
        saveTripButton = view.findViewById(R.id.save_trip_button)
        saveTripButton.setOnClickListener {
            saveTrip()
        }
        tripService = TripService()

        return view
    }

    private fun saveTrip() {
        val tripName = tripNameEditText.text.toString()
        val tripDate = tripDateEditText.text.toString()
        val tripDescription = tripDescriptionEditText.text.toString()
        val tripRating = tripRatingBar.rating

        val tripStartDate = this.tripStartDate
        val tripEndDate = this.tripEndDate

        if (tripName.isNotEmpty() && tripDate.isNotEmpty() && tripStartDate != null && tripEndDate != null) {
            val trip = TripModel(
                description = tripDescription,
                name = tripName,
                startDate = tripStartDate,
                endDate = tripEndDate,
                rating = tripRating,
            )

            tripService.createTrip(trip, onResponse = { responseMessage ->
                requireActivity().runOnUiThread {
                    if (responseMessage == "success") {
                        Toast.makeText(context, "Trip Saved", Toast.LENGTH_LONG).show()
                        // Optionally, navigate back or clear the form
                    } else {
                        Toast.makeText(context, "Error saving trip", Toast.LENGTH_LONG).show()
                    }
                }
            }, onFailure = { throwable ->
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select date range")
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tripStartDate = Date(selection.first ?: 0)
            tripEndDate = Date(selection.second ?: 0)
            val startDate = dateFormat.format(tripStartDate)
            val endDate = dateFormat.format(tripEndDate)
            val dateRange = "$startDate - $endDate"
            tripDateEditText.setText(dateRange)
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun getLoggedUserId(): Int {
        val sharedPreferences = requireActivity().getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1)
    }
}

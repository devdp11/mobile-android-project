package com.example.android_studio_project.fragment.trip.add_trip

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import java.util.UUID

class AddTripFragment : Fragment() {

    private lateinit var tripNameEditText: EditText
    private lateinit var tripDateEditText: EditText
    private lateinit var tripDescriptionEditText: EditText
    private lateinit var tripRatingBar: RatingBar

    private lateinit var saveTripButton: Button
    private lateinit var tripService: TripService

    private var tripStartDate: Date? = null
    private var tripEndDate: Date? = null

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
        tripService = TripService(requireContext())

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
                name = tripName,
                description = tripDescription,
                startDate = tripStartDate,
                endDate = tripEndDate,
                rating = tripRating
            )

            // Logar os dados do trip para verificação
            Log.d("AddTripFragment", "Trip Data: $trip")

            tripService.createTrip(trip, onResponse = { responseMessage ->
                requireActivity().runOnUiThread {
                    if (responseMessage == "success") {
                        Toast.makeText(context, "Trip Saved", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Error saving trip", Toast.LENGTH_LONG).show()
                    }
                }
            }, onFailure = { throwable ->
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                    Log.e("AddTripFragment", "Error creating trip", throwable)
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

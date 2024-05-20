package com.example.android_studio_project.fragment.trip.add_trip

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.android_studio_project.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTripFragment : Fragment(), CustomDatePickerFragment.OnDateSelectedListener {

    private lateinit var tripNameEditText: EditText
    private lateinit var tripDateEditText: EditText
    private lateinit var tripRatingBar: RatingBar
    private lateinit var addPhotosButton: Button
    private lateinit var photosGridView: GridView
    private lateinit var saveTripButton: Button

    private val photosList = mutableListOf<Uri>()
    private lateinit var photosAdapter: PhotosAdapter

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
        tripRatingBar = view.findViewById(R.id.trip_rating)
        addPhotosButton = view.findViewById(R.id.add_photos_button)
        photosGridView = view.findViewById(R.id.photos_grid)
        saveTripButton = view.findViewById(R.id.save_trip_button)

        photosAdapter = PhotosAdapter()
        photosGridView.adapter = photosAdapter

        addPhotosButton.setOnClickListener {
            openGallery()
        }

        saveTripButton.setOnClickListener {
            saveTrip()
        }

        return view
    }

    private val selectPhotosLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        photosList.addAll(uris)
        photosAdapter.notifyDataSetChanged()
    }

    private fun openGallery() {
        selectPhotosLauncher.launch("image/*")
    }

    private fun saveTrip() {
        val tripName = tripNameEditText.text.toString()
        val tripDate = tripDateEditText.text.toString()
        val tripRating = tripRatingBar.rating

        // bade de dados aqui
        Toast.makeText(context, "Trip Saved: $tripName, Date: $tripDate, Rating: $tripRating", Toast.LENGTH_LONG).show()
    }

    inner class PhotosAdapter : BaseAdapter() {
        override fun getCount(): Int = photosList.size

        override fun getItem(position: Int): Uri = photosList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val imageView = convertView as? ImageView ?: ImageView(requireContext()).apply {
                layoutParams = AbsListView.LayoutParams(300, 300)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            imageView.setImageURI(getItem(position))
            return imageView
        }
    }

    private fun showDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select trip date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.format(Date(selection))
            tripDateEditText.setText(date)
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    override fun onDateSelected(year: Int, month: Int, day: Int) {
        val selectedDate = "$day/${month + 1}/$year"
        tripDateEditText.setText(selectedDate)
    }
}

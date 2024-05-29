package com.example.android_studio_project.fragment.location.add_location

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.LocationModel
import com.example.android_studio_project.data.retrofit.services.LocationService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class add_location(private val tripUuid: UUID) : Fragment() {

    private lateinit var photosGridView: GridView
    private val photosList = mutableListOf<Uri>()
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var locationTypeSpinner: Spinner
    private lateinit var locationService: LocationService
    private val locationTypeMap = mutableMapOf<String, UUID>()
    private lateinit var uuidTextView: TextView
    private lateinit var dateTextInput : EditText

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_location, container, false)

        uuidTextView = view.findViewById(R.id.uuid_teste)
        uuidTextView.text = tripUuid.toString()

        locationService = LocationService(requireContext())
        locationTypeSpinner = view.findViewById(R.id.location_type)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationTypeSpinner.adapter = adapter

        getTypes(adapter)

        locationTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTypeName = parent.getItemAtPosition(position) as String
                val selectedTypeUuid = locationTypeMap[selectedTypeName]
                uuidTextView.text = selectedTypeUuid.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                uuidTextView.text = tripUuid.toString()
            }
        }

        val addPhotosButton: Button = view.findViewById(R.id.add_photos)
        photosGridView = view.findViewById(R.id.photos_grid)

        photosAdapter = PhotosAdapter()
        photosGridView.adapter = photosAdapter

        addPhotosButton.setOnClickListener {
            openGallery()
        }

        val saveBtn: Button = view.findViewById(R.id.save_btn)
        saveBtn.setOnClickListener {
            saveLocation()
        }

        dateTextInput = view.findViewById(R.id.location_date)
        dateTextInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (dateTextInput.right - dateTextInput.compoundDrawables[drawableEnd].bounds.width())) {
                    showDatePicker()
                    return@setOnTouchListener true
                }
            }
            false
        }

        return view
    }

    private fun showDatePicker() {
        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }

            val selectedDateFormatted = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(selectedCalendar.time)

            dateTextInput.setText(selectedDateFormatted)
        }, currentYear, currentMonth, currentDay)

        datePickerDialog.show()
    }

    private val selectPhotosLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        photosList.addAll(uris)
        photosAdapter.notifyDataSetChanged()
    }

    private fun getTypes(adapter: ArrayAdapter<String>) {
        locationService.getAllTypes(
            onResponse = { types ->
                if (types != null) {
                    val typeNames = types.map { it.name ?: "Unknown" }
                    adapter.clear()
                    adapter.addAll(typeNames)
                    adapter.notifyDataSetChanged()

                    types.forEach { type ->
                        if (type.name != null) {
                            locationTypeMap[type.name] = type.uuid
                        }
                    }
                }
            },
            onFailure = {
            }
        )
    }

    private fun saveLocation() {
        val selectedTypeName = locationTypeSpinner.selectedItem as? String
        val selectedTypeUuid = locationTypeMap[selectedTypeName]
        val locationName = view?.findViewById<EditText>(R.id.location_name)?.text.toString()
        val locationDescription = view?.findViewById<EditText>(R.id.location_description)?.text.toString()
        val locationRating = view?.findViewById<RatingBar>(R.id.location_rating)?.rating ?: 0.0f
        val locationDate = dateTextInput.text.toString()

        if (selectedTypeUuid != null && locationName.isNotEmpty() && locationDescription.isNotEmpty() && locationDate.isNotBlank()) {
            val location = LocationModel(
                uuid = UUID.randomUUID(),
                name = locationName,
                description = locationDescription,
                typeId = selectedTypeUuid,
                rating = locationRating,
                latitude = null,
                longitude = null,
                date = locationDate
            )

            locationService.createLocation(location,
                onResponse = { message ->
                    Log.d("add_location", "Uploaded location successfully. Response: $message")
                },
                onFailure = { error ->
                    Log.e("add_location", "Failed to upload location. Error: ${error.message}", error)
                }
            )
        } else {
            Toast.makeText(requireContext(), getString(R.string.fill_fields), Toast.LENGTH_LONG).show()
        }
    }

    private fun openGallery() {
        selectPhotosLauncher.launch("image/*")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    companion object {
        fun newInstance(tripUuid: UUID): add_location {
            return add_location(tripUuid)
        }
    }
}

package com.example.android_studio_project.fragment.location.edit_location

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.services.LocationService
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class edit_location(private val locationUuid: UUID, private val tripUuid: UUID) : Fragment() {

    private lateinit var photosGridView: GridView
    private val photosList = mutableListOf<Uri>()
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var dateEditText: EditText
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var locationTypeSpinner: Spinner
    private lateinit var locationService: LocationService
    private lateinit var uuidTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_location, container, false)

        uuidTextView = view.findViewById(R.id.uuid_teste)
        uuidTextView.text = locationUuid.toString()

        val deleteButton: Button = view.findViewById(R.id.delete_btn)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(tripUuid, locationUuid)
        }

        locationService = LocationService(requireContext())
        locationTypeSpinner = view.findViewById(R.id.location_type)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationTypeSpinner.adapter = adapter

        getTypes(adapter)

        val addPhotosButton : Button = view.findViewById(R.id.add_photos)
        photosGridView = view.findViewById(R.id.photos_grid)

        photosAdapter = PhotosAdapter()
        photosGridView.adapter = photosAdapter

        addPhotosButton.setOnClickListener {
            openGallery()
        }
        return view
    }

    private fun showDeleteConfirmationDialog(tripUuid: UUID, locationUuid: UUID) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_title))
        builder.setMessage(getString(R.string.delete_description))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            locationService.deleteLocation(tripUuid, locationUuid,
                onResponse = {
                    Toast.makeText(requireContext(), getString(R.string.trip_delete_succ), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                },
                onFailure = {
                    Toast.makeText(requireContext(), getString(R.string.trip_delete_error), Toast.LENGTH_SHORT).show()
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
                    Log.d("display_home", "Types received: $typeNames")
                } else {
                    Log.d("display_home", "No types received")
                }
            },
            onFailure = { error ->
                Log.e("display_home", "Failed to get types", error)
            }
        )
    }

    private fun openGallery() {
        selectPhotosLauncher.launch("image/*")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateEditText = view.findViewById(R.id.location_date)

        dateEditText.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateEditText.setText(selectedDate)
            }, year, month, day)
            datePickerDialog.show()
        }
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
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select date range")
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDate = dateFormat.format(Date(selection.first ?: 0))
            val endDate = dateFormat.format(Date(selection.second ?: 0))
            val dateRange = "$startDate - $endDate"
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    companion object {
        fun newInstance(locationUuid: UUID, tripUuid: UUID): edit_location {
            return edit_location(locationUuid, tripUuid)
        }
    }
}
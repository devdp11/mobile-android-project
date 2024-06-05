package com.example.android_studio_project.fragment.location.edit_location

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.LocationModelCreate
import com.example.android_studio_project.data.retrofit.models.PhotoModel
import com.example.android_studio_project.data.retrofit.services.LocationService
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class edit_location(private val locationUuid: UUID, private val tripUuid: UUID, ) : Fragment() {
    private lateinit var locationService: LocationService

    private lateinit var photosGridView: GridView
    private val photosList = mutableListOf<Uri>()
    private lateinit var photosAdapter: PhotosAdapter

    private lateinit var locationNameEditText: TextView
    private lateinit var locationDescriptionEditText: TextView
    private lateinit var locationTypeSpinner: Spinner
    private lateinit var locationRatingBar: RatingBar
    private lateinit var saveLocationButton: Button
    private lateinit var dateTextInput: TextView
    private val locationTypeMap = mutableMapOf<String, UUID>()

    private var locationDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_location, container, false)

        locationNameEditText = view.findViewById(R.id.location_name)
        locationDescriptionEditText = view.findViewById(R.id.location_description)
        locationTypeSpinner = view.findViewById(R.id.location_type)
        locationRatingBar = view.findViewById(R.id.location_rating)
        saveLocationButton = view.findViewById(R.id.save_btn)
        dateTextInput = view.findViewById(R.id.date_text_input)

        saveLocationButton.setOnClickListener {
            saveLocation()
        }

        dateTextInput.setOnClickListener {
            showDatePicker()
        }

        val deleteButton: Button = view.findViewById(R.id.delete_btn)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(locationUuid)
        }

        val addPhotoButton: Button = view.findViewById(R.id.add_photos)
        addPhotoButton.setOnClickListener {
            openGallery()
        }

        photosGridView = view.findViewById(R.id.photos_grid)
        photosAdapter = PhotosAdapter()
        photosGridView.adapter = photosAdapter

        locationService = LocationService(requireContext())
        getTypes()

        locationService.getLocationById(locationUuid,
            onResponse = { locationDetails ->
                locationDetails?.let {
                    val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

                    locationNameEditText.text = locationDetails.name ?: ""
                    locationDescriptionEditText.text = locationDetails.description ?: ""

                    locationDetails.date?.let { locationDate ->
                        val formattedDate = isoDateFormat.parse(locationDate)
                        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateDisplay = displayFormat.format(formattedDate)
                        dateTextInput.text = dateDisplay


                    }

                    locationDetails.rating?.let { rating ->
                        locationRatingBar.rating = rating
                    }
                }
            },
            onFailure = {
                Toast.makeText(context, getString(R.string.load_user_error), Toast.LENGTH_SHORT).show()
            }
        )


        locationService.getPhotoByLocationId(locationUuid,
            onResponse = { photoDetails ->
                photoDetails?.let { photos ->
                    val bitmapList = mutableListOf<Bitmap>()
                    photos.forEach { photo ->
                        val bitmap = decodeBase64ToBitmap(photo.data)
                        bitmap?.let { decodedBitmap ->
                            bitmapList.add(decodedBitmap)
                        }
                    }
                    photosAdapter.setData(bitmapList)
                }
            },
            onFailure = {
                Toast.makeText(context, getString(R.string.load_user_error), Toast.LENGTH_SHORT).show()
                Log.e("FOTOS", "Error deleting location")
            }
        )

        return view


    }

    private fun decodeBase64ToBitmap(encodedString: String?): Bitmap? {
        encodedString?.let {
            val decodedBytes = Base64.decode(it, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
        return null
    }

    private fun showDeleteConfirmationDialog(uuid: UUID) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_title))
        builder.setMessage(getString(R.string.delete_description))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            locationService.deleteLocation(tripUuid, uuid,
                onResponse = {
                    Toast.makeText(requireContext(), getString(R.string.trip_delete_succ), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), getString(R.string.trip_delete_error), Toast.LENGTH_SHORT).show()
                    Log.e("DeleteLocation", "Error deleting location: $error")
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

    private fun saveLocation() {
        val locationName = locationNameEditText.text.toString()
        val locationDescription = locationDescriptionEditText.text.toString()
        val locationRating = locationRatingBar.rating
        val selectedTypeName = locationTypeSpinner.selectedItem as? String
        val selectedTypeUuid = locationTypeMap[selectedTypeName]

        if (locationName.isNotEmpty()) {
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val formattedDate = isoDateFormat.format(isoDateFormat.parse(locationDate!!))

            val updatedLocation = LocationModelCreate(
                name = locationName,
                description = locationDescription,
                date = formattedDate,
                rating = locationRating,
                uuid = locationUuid,
                latitude = null,
                longitude = null,
                typeId = selectedTypeUuid
            )

            locationService.updateLocation(locationUuid, updatedLocation, onResponse = { responseMessage, locationUUID ->
                requireActivity().runOnUiThread {
                    if (responseMessage == "success") {
                        showConfirmationDialog()
                    } else {
                        Toast.makeText(context, "Error updating location", Toast.LENGTH_LONG).show()
                        Log.e("LOCATION", "$updatedLocation")
                    }
                }
            }, onFailure = { throwable ->
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                    Log.e("EditLocationFragment", "Error updating location", throwable)
                    Log.e("LOCATION", "$updatedLocation")
                }
            })
        } else {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
        }
    }


    private val selectPhotosLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        photosList.addAll(uris)
        photosAdapter.notifyDataSetChanged()
    }

    private fun openGallery() {
        selectPhotosLauncher.launch("image/*")
    }

    inner class PhotosAdapter : BaseAdapter() {
        private var bitmapList = mutableListOf<Bitmap>()

        fun setData(bitmaps: List<Bitmap>) {
            bitmapList.clear()
            bitmapList.addAll(bitmaps)
            notifyDataSetChanged()
        }

        override fun getCount(): Int = bitmapList.size

        override fun getItem(position: Int): Bitmap = bitmapList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val imageView = convertView as? ImageView ?: ImageView(requireContext()).apply {
                layoutParams = AbsListView.LayoutParams(300, 300)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            imageView.setImageBitmap(getItem(position))
            return imageView
        }
    }


    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Success")
        builder.setMessage("Location updated successfully.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            parentFragmentManager.popBackStack()
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun getTypes() {
        locationService.getAllTypes(
            onResponse = { types ->
                if (types != null) {
                    val typeNames = types.map { it.name ?: "Unknown" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    locationTypeSpinner.adapter = adapter
                } else {
                    Log.d("edit_location", "No types received")
                }
            },
            onFailure = { error ->
                Log.e("edit_location", "Failed to get types", error)
            }
        )
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            locationDate = isoDateFormat.format(Date(selection ?: 0))

            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateDisplay = displayFormat.format(Date(selection ?: 0))
            dateTextInput.setText(dateDisplay)
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }


    companion object {
        fun newInstance(locationUuid: UUID, tripUuid: UUID): edit_location {
            return edit_location(locationUuid, tripUuid)
        }
    }
}
